package com.webapplication.controller;

import com.webapplication.authentication.Authenticator;
import com.webapplication.dao.elasticRepository.ElasticEventRepository;
import com.webapplication.dao.jpaRepository.*;
import com.webapplication.dto.event.*;
import com.webapplication.elasticEntity.ElasticEventEntity;
import com.webapplication.entity.*;
import com.webapplication.error.event.EventCommentSubmitError;
import com.webapplication.error.event.NewBookingSubmitError;
import com.webapplication.dao.jpaRepository.EventRepository;
import com.webapplication.dao.jpaRepository.PhotosRepository;
import com.webapplication.dao.jpaRepository.ProviderRepository;
import com.webapplication.dto.event.*;
import com.webapplication.elasticEntity.ElasticEventEntity;
import com.webapplication.entity.EventEntity;
import com.webapplication.entity.EventPhotosEntity;
import com.webapplication.entity.ProviderEntity;
import com.webapplication.exception.NotAuthorizedException;
import com.webapplication.exception.ValidationException;
import com.webapplication.error.event.EventError;
import com.webapplication.error.user.UserError;
import com.webapplication.exception.user.ForbiddenException;
import com.webapplication.exception.user.NotAuthenticatedException;
import com.webapplication.mapper.BookingMapper;
import com.webapplication.mapper.CommentEventMapper;
import com.webapplication.mapper.EventMapper;
import com.webapplication.validator.event.EventCommentValidator;
import com.webapplication.validator.event.EventRequestValidator;
import com.webapplication.validator.event.NewBookingValidator;
import org.elasticsearch.common.geo.GeoDistance;
import org.elasticsearch.common.unit.DistanceUnit;
import org.elasticsearch.common.unit.Fuzziness;
import org.elasticsearch.index.query.*;
import org.joda.time.DateTime;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.elasticsearch.core.geo.GeoPoint;
import org.springframework.data.elasticsearch.core.query.NativeSearchQueryBuilder;
import org.springframework.data.elasticsearch.core.query.SearchQuery;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Component;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import javax.servlet.http.HttpServletResponse;
import java.io.*;
import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.Iterator;
import java.sql.Timestamp;

import static org.elasticsearch.index.query.QueryBuilders.multiMatchQuery;

/**
 * Created by mary on 5/6/2017.
 */

@Component
public class EventControllerImpl implements EventController{
	@Autowired
	private EventRepository eventRepository;
	@Autowired
	private EventDateRepository eventDateRepository;
	@Autowired
	private BookingRepository bookingRepository;
	@Autowired
	private ProviderRepository providerRepository;

	@Autowired
	private PhotosRepository photosRepository;
	@Autowired
	private CommentEventRepository commentEventRepository;

	@Autowired
	private EventMapper eventMapper;
	@Autowired
	private BookingMapper bookingMapper;
	@Autowired
	private CommentEventMapper commentEventMapper;

	@Autowired
	private EventRequestValidator eventRequestValidator;
	@Autowired
	private NewBookingValidator newBookingValidator;
	@Autowired
	private EventCommentValidator eventCommentValidator;

	@Autowired
	private Authenticator authenticator;

	private ParentRepository parentRepository;
	@Autowired
	private ElasticEventRepository elasticEventRepository;


	@Override
	public EventResponseDto getEvent(@PathVariable Integer eventId) throws Exception {
		Optional.ofNullable(eventId).orElseThrow(() -> new ValidationException(EventError.MISSING_DATA));

		//Get EventEntity
		EventEntity event = eventRepository.findEventsById(eventId);
		return  eventMapper.eventToEventResponse(event);
	}

	@Override
	public EventSubmitResponseDto submitEvent(@RequestHeader UUID authToken, @RequestBody EventSubmitRequestDto eventSubmitRequestDto) throws Exception {
		System.out.println(eventSubmitRequestDto);
		Optional.ofNullable(authToken).orElseThrow(() -> new ValidationException(UserError.MISSING_DATA));

		eventRequestValidator.validate(eventSubmitRequestDto);
		ProviderEntity providerEntity = providerRepository.findProviderByUserId(eventSubmitRequestDto.getProvider());
		if (!providerEntity.getUser().getId().equals(authenticator.checkUpdateSession(authToken).getUserId())){
		//if (authenticator.getSession(authToken).getUserId() != providerEntity.getUser().getId()){
			throw new NotAuthorizedException(UserError.UNAUTHORIZED);
		}

		EventEntity eventEntity= eventMapper.eventEntityFromEventDto(eventSubmitRequestDto);
		eventEntity.setProvider(providerEntity);
		Timestamp timestamp = new Timestamp(System.currentTimeMillis());
		eventEntity.setDate_created(timestamp);
		eventRepository.saveAndFlush(eventEntity);

		if (eventSubmitRequestDto.getPhotos() != null) {
			List<String> photos = eventSubmitRequestDto.getPhotos();


			photos.forEach(photo -> {
				EventPhotosEntity photosEntity = new EventPhotosEntity();

				photosEntity.setEvent(eventEntity);
				photosEntity.setPhoto_path(photo);

				System.out.println(photo);

				photosRepository.saveAndFlush(photosEntity);
			});
		};

		Date startDate=eventSubmitRequestDto.getDate_starting();
		Date x = new Date();
		Date y= new Date();
		GeoPoint location=new GeoPoint(eventSubmitRequestDto.getLatitude(), eventSubmitRequestDto.getLongitude());

		ElasticEventEntity elasticEventEntity = new ElasticEventEntity(eventEntity.getId().toString(),eventEntity.getName()
				,eventEntity.getDescription(),
				eventEntity.getProvider().getUser().getName(),eventEntity.getProvider().getCompanyName(),startDate,y,location);
		elasticEventRepository.save(elasticEventEntity);

		EventSubmitResponseDto response = new EventSubmitResponseDto(HttpStatus.OK,"Event registered succesfully");
		return  response;
	}

	@Override
	public List<ElasticEventEntity> searchEvents(@RequestBody EventFreeTextSearchDto eventFreeTextSearchDto) throws Exception {

QueryBuilder queryBuilder =              // Path
				QueryBuilders.boolQuery()       // Your query
						.must(QueryBuilders.multiMatchQuery(eventFreeTextSearchDto.getText()).field("name")
								.field("providerName")
								.field("company")
								.field("description")
								.type(MultiMatchQueryBuilder.Type.BEST_FIELDS).fuzziness(Fuzziness.TWO)
						)
						.must(QueryBuilders.rangeQuery("startingDate")
								.from(eventFreeTextSearchDto.getStartingDate())
								.to(eventFreeTextSearchDto.getEndingDate()))
						.must(QueryBuilders.geoDistanceQuery("location")
								.point(eventFreeTextSearchDto.getLatitude(),eventFreeTextSearchDto.getLongitude())
								.distance(eventFreeTextSearchDto.getKm(), DistanceUnit.KILOMETERS)
								.optimizeBbox("memory")
								.geoDistance(GeoDistance.ARC));


		SearchQuery searchQuery=new NativeSearchQueryBuilder().withQuery(queryBuilder).build();
		return elasticEventRepository.search(searchQuery).getContent();
	}

	@Override
	public UploadFileResponseDto UploadFile(MultipartHttpServletRequest request) throws IOException {

		Iterator<String> itr = request.getFileNames();
		MultipartFile file = request.getFile(itr.next());
		String fileName = file.getOriginalFilename();

		File dir = new File("./src/main/resources/static/images/user_uploads");
		if (dir.isDirectory()) {

			File serverFile = new File(dir, fileName);

			BufferedOutputStream stream = new BufferedOutputStream(
					new FileOutputStream(serverFile));
			stream.write(file.getBytes());
			stream.close();

			String path = "images/user_uploads/" + fileName;
			UploadFileResponseDto uploadFileResponseDto = new UploadFileResponseDto();
			uploadFileResponseDto.setPath(path);
			return uploadFileResponseDto;
		} else {
			return null;
		}

	}


	@Override
	public NewBookingResponseDto bookEvent(@RequestHeader UUID authToken, @RequestBody NewBookingRequestDto newBookingRequestDto) throws  Exception{

		Optional.ofNullable(authToken).orElseThrow(() -> new ValidationException(UserError.MISSING_DATA));
		newBookingValidator.validate(newBookingRequestDto);
		ParentEntity parentEntity = parentRepository.findParentByUserId(newBookingRequestDto.getParent_id());
		if (!parentEntity.getUser().getId().equals(authenticator.checkUpdateSession(authToken).getUserId())){
		//if (authenticator.getSession(authToken).getUserId() != parentEntity.getUser().getId()){
			throw new NotAuthorizedException(UserError.UNAUTHORIZED);
		}
		EventDateEntity eventDateEntity = eventDateRepository.findEventDateById(newBookingRequestDto.getEventDate_id());
		if (eventDateEntity == null){
			throw new ValidationException(NewBookingSubmitError.INVALID_DATA);
		}
		if (eventDateEntity.getAvailable_tickets() < newBookingRequestDto.getNumOfTickets()){
			throw new ValidationException(NewBookingSubmitError.NOT_ENOUGH_TICKETS);
		}
		if (parentEntity.getPoints() < eventDateEntity.getEvent().getTicket_price()*newBookingRequestDto.getNumOfTickets()){
			throw new ValidationException(NewBookingSubmitError.NOT_ENOUGH_POINTS);
		}

		parentEntity.setPoints(parentEntity.getPoints()-eventDateEntity.getEvent().getTicket_price()*newBookingRequestDto.getNumOfTickets());
		eventDateEntity.setAvailable_tickets(eventDateEntity.getAvailable_tickets() - newBookingRequestDto.getNumOfTickets());
		if(eventDateEntity.getTickets_sold() == null)
			eventDateEntity.setTickets_sold(newBookingRequestDto.getNumOfTickets());
		else
			eventDateEntity.setTickets_sold(eventDateEntity.getTickets_sold() + newBookingRequestDto.getNumOfTickets());
		BookingEntity bookingEntity = bookingMapper.bookingEntityFromBookingRequestDto(newBookingRequestDto, parentEntity, eventDateEntity);
		bookingEntity.setBooking_time(new Timestamp(System.currentTimeMillis()));
		bookingRepository.saveAndFlush(bookingEntity);
		NewBookingResponseDto response = new NewBookingResponseDto(HttpStatus.OK, "Tickets successfully booked");

		return response;

	}


	@Override
	public SubmitEventCommentResponseDto submitComment(@RequestHeader UUID authToken, SubmitEventCommentRequestDto submitEventCommentRequestDto) throws Exception{

		Optional.ofNullable(authToken).orElseThrow(() -> new ValidationException(UserError.MISSING_DATA));
		eventCommentValidator.validate(submitEventCommentRequestDto);
		ParentEntity parentEntity = parentRepository.findParentByUserId(submitEventCommentRequestDto.getUser_id());
		if (!parentEntity.getUser().getId().equals(authenticator.checkUpdateSession(authToken).getUserId())){
		//if (authenticator.getSession(authToken).getUserId() != parentEntity.getUser().getId()){
			throw new NotAuthorizedException(UserError.UNAUTHORIZED);
		}
		EventEntity eventEntity = eventRepository.findEventsById(submitEventCommentRequestDto.getEvent_id());
		if (eventEntity == null){
			throw new ValidationException(EventCommentSubmitError.EVENT_NOT_EXISTS);
		}

		CommentEventEntity commentEventEntity = commentEventMapper.commentEventEntityFromSubmitCommentEventDto(submitEventCommentRequestDto, parentEntity, eventEntity);
		commentEventEntity.setDate(new Timestamp(System.currentTimeMillis()));
		commentEventRepository.saveAndFlush(commentEventEntity);
		SubmitEventCommentResponseDto response = new SubmitEventCommentResponseDto(HttpStatus.OK, "Comment submitted successfully");

		return response;
	}

	@ExceptionHandler(ValidationException.class)
	private void invalidAttributes(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.BAD_REQUEST.value());
	}

	@ExceptionHandler(NotAuthenticatedException.class)
	private void userNotFound(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.UNAUTHORIZED.value());
	}

	@ExceptionHandler({NotAuthorizedException.class, ForbiddenException.class})
	private void forbiddenAction(HttpServletResponse response) throws IOException {
		response.sendError(HttpStatus.FORBIDDEN.value());
	}

	@ExceptionHandler(Exception.class)
	private void genericError(HttpServletResponse response) throws IOException {//todo we do not see errors in console
		response.sendError(HttpStatus.INTERNAL_SERVER_ERROR.value());
	}

}



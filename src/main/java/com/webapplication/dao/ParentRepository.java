package com.webapplication.dao;

import com.webapplication.entities.Parent;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Created by KechagiasKonstantinos on 28/05/2017.
 */
public interface ParentRepository extends JpaRepository<Parent,Long> {
    Parent findByEmail(String email);
}



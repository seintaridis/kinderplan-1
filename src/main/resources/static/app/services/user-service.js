router.factory('UserService', function($http, $cookies) {
    var userService = {};

    userService.login = function (user) {
        return $http.post('/api/login', user)
            .then(function (response) {
                console.log(response);
                $cookies.put('id', response.data.userId);
                $cookies.put('role', response.data.role);
                $cookies.put('signedIn', 'yes');
                $cookies.put('authToken', response.data.authToken);

                return response;
            });

    };

    userService.getuser = function(userId, authToken){
        return $http.get('/api/user/' + userId, {headers: {'authToken': authToken}})
            .then(function(response){
                return response;
            });
    };

    userService.createUser = function(request){
        return $http.post('/api/signup/',request)
            .then(function(response){
                return response;
            });
    };


    userService.pay = function(data, authToken){
        console.log(authToken);
        console.log(data);
        return $http.post('/api/pay', data, {headers: {'authToken': authToken}})
            .then(function(response){
                console.log(response);
                return response;
            });
    };

    userService.getProfile = function(id){
        return $http.get('/api/user/viewProfile/' + id)
            .then(function(response){
                return response;
            });
    }

    return userService;
});
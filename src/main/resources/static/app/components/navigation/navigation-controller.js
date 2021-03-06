router.controller('navigationController', function($scope, $http, $cookies, UserService){
    $scope.user = {};
    $scope.signedIn = {};
    $scope.donotmatch = false;

    if($cookies.get('signedIn') === 'yes'){
        $scope.signedIn = true;
        $scope.user.userId = $cookies.get('id');
        $scope.user.authToken = $cookies.get('authToken');

        UserService.getuser($scope.user.userId ,$scope.user.authToken)
            .then(function(response){
                $scope.user = response.data;
            }, function(response){
                $cookies.remove('id');
                $cookies.remove('role');
                $cookies.remove('authToken');

                $cookies.put('signedIn', 'no');
                $scope.signedIn = false;
                $scope.user = {};
            });
    }else {
        $cookies.remove('id');
        $cookies.remove('role');
        $cookies.remove('authToken');

        $cookies.put('signedIn', 'no');
        $scope.signedIn = false;
        $scope.user = {};
    }

    $scope.signout = function(){
        $cookies.remove('id');
        $cookies.remove('role');
        $cookies.remove('authToken');

        $cookies.put('signedIn', 'no');
        $scope.signedIn = false;
        $scope.user = {};
        $scope.signinform.$submitted = false;
    };

    $scope.login = function(user){


        UserService.login(user)
            .then(function(response){
                UserService.getuser(response.data.userId ,response.data.authToken)
                    .then(function(response){
                        $scope.signedIn = true;
                        $scope.user = response.data;

                    }, function(response){
                        console.log(response);
                    });


                $('#signin-modal').modal('hide');

            }, function(error){
                console.log(error);
                $scope.donotmatch = true;
            });


    };


});



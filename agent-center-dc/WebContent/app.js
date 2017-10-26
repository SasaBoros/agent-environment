var appModule = angular.module('app' , ['ngRoute', 'rsMessagingModule', 'wsMessagingModule']);

appModule.config(function($routeProvider) {
    $routeProvider
        .when('/rs', {
            templateUrl : 'Messaging/messaging.html',
            controller  : 'rsMessageController'
        })
        .when('/ws', {
            templateUrl : 'Messaging/messaging.html',
            controller  : 'wsMessageController'
        })
});

appModule.controller('appController', ['$scope', '$window', function($scope, $window) {
	
	$scope.protocol = "";
	
	$scope.setController = function(controller) {
		if(controller == 'Rest') {
			$window.location.href = "#!/rs";
		}
		else if(controller == 'Websocket') {
			$window.location.href = "#!/ws";
		}
	}
}]);




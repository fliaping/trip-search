/**
 * Created by Payne on 4/11/16.
 */
var baseUrl = "http://localhost:9090/ss?";
var app = angular.module('searchIndex', ['ionic']);

app.run(['$ionicPlatform',function($ionicPlatform) {
    $ionicPlatform.ready(function() {
        // Hide the accessory bar by default (remove this to show the accessory bar above the keyboard
        // for form inputs)
        if(window.cordova && window.cordova.plugins.Keyboard) {
            cordova.plugins.Keyboard.hideKeyboardAccessoryBar(true);
        }
        if(window.StatusBar) {
            StatusBar.styleDefault();
        }
    });
}]);
app.controller("indexCtrl",['$state',function ($state) {
    $state.go("home");
}]);
app.controller("homeCtrl",['$scope','$http','$location','$ionicModal','$timeout','$ionicSideMenuDelegate','$rootScope','$ionicHistory','SearchResult',
    function ($scope,$http,$location,$ionicModal,$timeout,$ionicSideMenuDelegate,$rootScope,$ionicHistory,SearchResult) {
        console.log("viewId"+$ionicHistory.currentHistoryId());
    //隐藏/打开侧边栏
    $scope.catSearchMenu = function () {
        $ionicSideMenuDelegate.toggleLeft(); //toggle侧边栏
    };

    //打开设置页
    $scope.openSetting = function () {
        // 转向设置页
    };

    // 搜索框模板载入
    $ionicModal.fromTemplateUrl('template/searchInput.html',function (modal) {
        $scope.searchModal = modal;
    },{
        scope: $scope,
        animation: 'slide-in-up'
    });

    //打开搜索输入页
    $scope.openSearch = function () {
        $scope.searchModal.show();
        $scope.focusSearchInput = true;
    };

    //关闭搜索输入页
    $scope.closeSearch = function () {
        $scope.focusSearchInput = false;
        $scope.searchModal.hide();
    };

    //执行搜索
    $scope.doSearch = function (search) {
        $scope.focusSearchInput = false;
        $scope.searchModal.hide();
        $location.path("/resultList/"+search.content);
        
    };
    /**
     * 附近热门景点
     */
    $scope.doRefresh = function () {
        $scope.nearSight();
    };
    //获取附近景点
    $scope.nearSight = function (moreIndex) {
        var latitude = window.localStorage['latitude'];
        var longitude = window.localStorage['longitude'];
        console.log(latitude+","+longitude+","+moreIndex);
        var nearSearchUrl = baseUrl+"query_type=near";
        if(latitude!=null && longitude!=null){
            nearSearchUrl+="&point="+latitude+","+longitude;
        }else {
            nearSearchUrl+="&point=22.5347168,113.971228";//默认坐标,世界之窗
        }
        if(moreIndex != null){
            //展开更多
            nearSearchUrl+="&page="+moreIndex;
        }
        $http.get(nearSearchUrl)
            .success(function (response) {

                if(moreIndex != null){
                    //新数据合并到前面的数据中
                    var temp = $rootScope.nsResults.sights;
                    $rootScope.nsResults = response;
                    $rootScope.nsResults.sights = temp.concat(response.sights);

                    //$scope.response.sights = $scope.response.sights.concat(response.sights);
                    console.log($rootScope.nsResults);
                }else {
                    $rootScope.nsResults = response;
                    SearchResult.save("near",response);
                }

            })
            .finally(function () {
                $scope.$broadcast('scroll.refreshComplete');
            });
    }
    //当前位置模板载入
    $ionicModal.fromTemplateUrl('template/location.html',function (modal) {
        $scope.locationModal = modal;
    },{
        scope: $scope,
        animation: 'slide-in-up'
    });
    //打开位置页
    $scope.openLocation = function () {
        $scope.locationModal.show(); //打开定位页
        if (navigator.geolocation) {
            navigator.geolocation.getCurrentPosition(locationSuccess, locationError,{
                // 指示浏览器获取高精度的位置，默认为false
                enableHighAccuracy: true,
                // 指定获取地理位置的超时时间，默认不限时，单位为毫秒
                timeout: 5000,
                // 最长有效期，在重复获取地理位置时，此参数指定多久再次获取位置。
                maximumAge: 3000
            });
        }else{
            alert("Your browser does not support Geolocation!");
        }
        function locationSuccess(position) {
            var coords = position.coords;
            window.localStorage['latitude'] = coords.latitude;
            window.localStorage['longitude'] = coords.longitude;
            $scope.closeLocation();
            console.log(coords.latitude+","+coords.longitude);
            //$scope.locationinfo = "lat:"+coords.latitude+"<br/> lng:"+coords.longitude;
        }
        function locationError(error){
            switch(error.code) {
                case error.TIMEOUT:
                    showError("A timeout occured! Please try again!");
                    break;
                case error.POSITION_UNAVAILABLE:
                    showError('We can\'t detect your location. Sorry!');
                    break;
                case error.PERMISSION_DENIED:
                    showError('Please allow geolocation access for this to work.');
                    break;
                case error.UNKNOWN_ERROR:
                    showError('An unknown error occured!');
                    break;
            }
        }
    };

    //关闭位置页
    $scope.closeLocation = function () {
        $scope.locationModal.hide();
    };

    //确定位置
    $scope.doLocation = function (search) {
        $scope.locationModal.hide();

    };
        //加载时执行
        $scope.doRefresh();
}]);

app.controller('searchInputCtrl',['$scope','$http','$timeout',function ($scope,$http,$timeout) {

}]);

app.controller('resultListCtrl',['$scope','$ionicModal','$ionicBackdrop','$http','$timeout','$stateParams','$ionicLoading',
                '$ionicSideMenuDelegate','$ionicPopover','$rootScope','SearchResult',
    function ($scope,$ionicModal,$ionicBackdrop,$http,$timeout,$stateParams,$ionicLoading,
              $ionicSideMenuDelegate,$ionicPopover,$rootScope,SearchResult) {
        var URL = {keySearch:"",sightType:"",sortType:""};
        URL.toString = function () {
            return baseUrl + URL.keySearch
                            + URL.sightType
                            + URL.sortType;
        };
        //隐藏/打开侧边栏
        $scope.catFacetMenu = function () {
            $ionicSideMenuDelegate.toggleRight();
        };
        $scope.search = function (searchKey) {
            $scope.searchKey = searchKey;

            if(searchKey != null){
                URL.keySearch = "query_type=key&keyword="+searchKey;
                // Setup the loader
                $ionicLoading.show({
                    content: 'Loading',
                    animation: 'fade-in',
                    showBackdrop: true,
                    maxWidth: 200,
                    showDelay: 0
                });
                console.log(URL.sightType);
                $http.get(URL.toString())
                    .success(function (response) {
                        $ionicLoading.hide();
                        $rootScope.ksResults = response;
                        SearchResult.save("key",response);
                    });
            }else {

            }
        };

        $scope.search($stateParams.searchKey);


        // 创建并载入模板
        $ionicModal.fromTemplateUrl('template/searchInput.html',function (modal) {
            $scope.searchModal = modal;
        },{
            scope: $scope,
            animation: 'slide-in-up'
        });

        //打开搜索输入页
        $scope.openSearch = function () {
            $scope.searchModal.show();
            $scope.focusSearchInput = true;
        };

        //关闭搜索输入页
        $scope.closeSearch = function () {
            $scope.focusSearchInput = false;
            $scope.searchModal.hide();
        };

        //执行搜索
        $scope.doSearch = function (search) {
            $scope.focusSearchInput = true;
            $scope.searchModal.hide();
            $scope.search(search.content);
        };

        //筛选
            $scope.showSightType = function () {
                $scope.facetview = {
                    type : "sightType",
                    hidehome : true ,
                    showsightType : true,
                    typeUncheck : true,
                    typeCheckAll : false,
                    showCompleteButton : true
                };
                $scope.sightFacets = $rootScope.ksResults.sightFacets;
                console.log($rootScope.ksResults);
            };
            //景点类型全选
            $scope.typeCheckButton = function (facetview) {
                switch (facetview.type){
                    case 'sightType':
                        var items = $scope.sightFacets[0].items;
                        if($scope.facetview.typeUncheck){ //不是全选
                            $scope.facetview.typeUncheck = false;
                            $scope.facetview.typeCheckAll = true;
                            for (x in items){
                                items[x].checked = true;
                            }
                        }else {  //若是全选状态
                            $scope.facetview.typeUncheck = true;
                            $scope.facetview.typeCheckAll = false;
                            for (x in items){
                                items[x].checked = false;
                            }
                        }
                    case '' :
                }


            };

            //选中
            $scope.checkTypeItemAction = function (item) {
              console.log(item);
                if(item.checked){

                }
            };
            //完成动作
            $scope.completeAction = function (facetview) {
                console.log(facetview.type);
                console.log($rootScope.ksResults);
                var items = $rootScope.ksResults.sightFacets[0].items;
                var sightType = "";
                for(x in items){
                   if(items[x].checked){
                       sightType += items[x].name + ",";
                   }
                }
                if(sightType != null){
                    sightType = sightType.substring(0,sightType.length - 1); //去掉最后的逗号

                    URL.sightType = "&sight_type="+sightType;

                    $http.get(URL.toString()).then(function successCallback(response) {
                        var tmp = $rootScope.ksResults.sightFacets; //保留原有facet
                        $rootScope.ksResults = response.data;
                        $rootScope.ksResults.sightFacets = tmp;
                        $scope.catFacetMenu();
                        console.log($rootScope.ksResults);
                    },function errorCallback(response) {

                    });
                }

            };
            // 排序
            //弹出popover
            $ionicPopover.fromTemplateUrl('templates/popover.html', {
                scope: $scope
            }).then(function(popover) {
                $scope.popover = popover;
                document.body.classList.add('platform-ios');
            });

            $scope.sortAction = function () {
                $scope.popover.hide();
                URL.sortType = "&sort_order=" + $scope.popover.sortType;
                $http.get(URL.toString()).then(function successCallback(response) {

                },function errorCallback(response) {

                });
            }



}]);

app.controller('sightDetailCtrl',['$rootScope','$scope','$http','$stateParams','SearchResult',
    function ($rootScope,$scope,$http,$stateParams,SearchResult) {

    var results;
    if($stateParams.type == 'near'){
        $scope.sight = SearchResult.getSightById("near",$stateParams.sightId,$rootScope.nsResults.sights);
    }else if ($stateParams.type == 'key'){
        $scope.sight = SearchResult.getSightById("key",$stateParams.sightId,$rootScope.ksResults.sights);
    }
    if ($scope.sight != null){
        //载入地图
        var coor = $scope.sight.coordinate.split(",");

        var map = new AMap.Map('container',{
            zoom: 10,
            center: [coor[1],coor[0]],
        });
        var marker = new AMap.Marker({
            position: [coor[1],coor[0]],
            map:map
        });
    }
}]);



app.config(['$stateProvider','$urlRouterProvider',function ($stateProvider,$urlRouterProvider) {
    $stateProvider
        .state('/',{
            url:"/",
            templateUrl: "index.html",
            controller: "indexCtrl"
        })
        .state('home',{
            url:"/home",
            templateUrl:"template/home.html",
            controller:'homeCtrl'
        })
        .state('searchInput',{
            url:'/searchInput',
            templateUrl:'template/searchInput.html',
            controller:'searchInputCtrl'
        })
        .state('resultList',{
            url:'/resultList/:searchKey',
            templateUrl:'template/resultList.html',
            controller:'resultListCtrl'
        })
        .state('sightDetail',{
            url:'/sightDetail/:type/:sightId',
            templateUrl:'template/sightDetail.html',
            controller:'sightDetailCtrl'
        });
    $urlRouterProvider.otherwise("/");
}]);

/**
 * 距离过滤器,大于1公里的显示公里,小于的显示米
 */
app.filter('distance',function () {
    var filter = function(input){
           if(input > 1){
               return input.toFixed(2)+" km";
           }else {
               return (input*1000).toFixed(0) + " m";
           }
        };
    return filter;
});

app.factory('SearchResult',function () {
    return {
        all: function (flag) {
            var resultString;
            switch (flag){
                case "near" : resultString = window.localStorage['nearSearch'];
                case "key" : resultString = window.localStorage['keySearch'];
            }

            if (resultString){
                return angular.fromJson(resultString);
            }
            return [];
        },
        save:function (flag,response) {
            switch (flag){
                case "near" : window.localStorage['nearSearch'] = angular.toJson(response);
                case "key" :  window.localStorage['keySearch'] = angular.toJson(response);
            }
        },
        getSightById : function (flag,sightId,SearchSights) {
            var sights;
            if(SearchSights == null){
                var resultString;
                switch (flag){
                    case "near" : resultString = window.localStorage['nearSearch'];
                    case "key" : resultString = window.localStorage['keySearch'];
                }
                if(resultString){
                    sights = angular.fromJson(resultString).sights;
                }
            }else {
                sights = SearchSights;
            }
            var sight;
            for ( x in sights){
                if(sights[x].id == sightId){
                    sight = sights[x];
                    break;
                }
            }
            return sight;

        }

    }
});

/**
 * Sets focus to this element if the value of focus-me is true.
 * @example
 *  <a ng-click="addName=true">add name</a>
 *  <input ng-show="addName" type="text" ng-model="name" focus-me="{{addName}}" />
 *
 *  add  <preference name="KeyboardDisplayRequiresUserAction" value="false" /> in config.xml
 */
app.directive('focusMe', ['$timeout', function($timeout) {
    return {
        scope: { trigger: '@focusMe' },
        link: function(scope, element) {
            scope.$watch('trigger', function(value) {
                if(value === "true") {
                    $timeout(function() {
                        element[0].focus();
                    },150);
                }
            });
        }
    };
}]);
/*app.directive('focusMe', function($timeout) {
    return {
        link: function(scope, element, attrs) {
            $timeout(function() {
                element[0].focus();
            }, 150);
        }
    };
});*/

/**
 * Created by Payne on 4/11/16.
 */
//var baseUrl = "http://localhost:9090/ss?";
var baseUrl = "http://stu-ali.xvping.cn:8080/search/ss?";
var app = angular.module('searchIndex', ['ionic','ngCordova']);

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
app.controller("homeCtrl",['$scope','$http','$location','$ionicModal','$ionicLoading','$timeout','$ionicSideMenuDelegate'
    ,'$rootScope','$ionicHistory','SearchResult','$sce','$cordovaGeolocation',
    function ($scope,$http,$location,$ionicModal,$ionicLoading,$timeout,$ionicSideMenuDelegate,$rootScope,$ionicHistory,SearchResult
        ,$sce,$cordovaGeolocation) {
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
            console.log("nowPage:"+$rootScope.nsResults.nowPage+" moreIndex:"+moreIndex);
            nearSearchUrl+="&page="+moreIndex;
            $scope.hidespinner = false;
        }
        $http.get(nearSearchUrl)
            .success(function (response) {
                $scope.hidespinner = true;
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

    //加载动画
    $scope.loading = function (isLoading) {
        if(isLoading){
            // Setup the loader
            $ionicLoading.show({
                content: 'Loading',
                animation: 'fade-in',
                showBackdrop: true,
                maxWidth: 200,
                showDelay: 0
            });
        }else {
            $ionicLoading.hide();
        }

    };
    //打开位置页
    $scope.openLocation = function () {
        //$scope.locationModal.show(); //打开定位页
        $scope.loading(true);
        var isIOS = ionic.Platform.isIOS();
        var isAndroid = ionic.Platform.isAndroid();
        console.log("Platform:"+ionic.Platform.platform());

        if(isAndroid || isIOS){
            var posOptions = {timeout: 10000, enableHighAccuracy: false};
            $cordovaGeolocation
                .getCurrentPosition(posOptions)
                .then(function (position) {
                    window.localStorage['latitude']  = position.coords.latitude;
                    window.localStorage['longitude'] = position.coords.longitude;
                    //$scope.closeLocation();
                    $scope.loading(false);
                }, function(err) {
                    // error
                });
        }else {
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
        }

        function locationSuccess(position) {
            var coords = position.coords;
            window.localStorage['latitude'] = coords.latitude;
            window.localStorage['longitude'] = coords.longitude;
            //$scope.closeLocation();
            $scope.loading(false);
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
                '$ionicSideMenuDelegate','$ionicPopover','$ionicPopup','$rootScope','SearchResult',
    function ($scope,$ionicModal,$ionicBackdrop,$http,$timeout,$stateParams,$ionicLoading,
              $ionicSideMenuDelegate,$ionicPopover,$ionicPopup,$rootScope,SearchResult) {
        var URL = {keySearch:"",sightType:"",sortType:"",point:"",distance:"",score_range:""};
        URL.toString = function () {
            return baseUrl + URL.keySearch
                            + URL.sightType
                            + URL.sortType
                            + URL.point
                            + URL.distance
                            + URL.score_range;
        };
        //隐藏/打开侧边栏
        $scope.catFacetMenu = function () {
            $ionicSideMenuDelegate.toggleRight();
        };
        //加载动画
        $scope.loading = function (isLoading) {
            if(isLoading){
                // Setup the loader
                $ionicLoading.show({
                    content: 'Loading',
                    animation: 'fade-in',
                    showBackdrop: true,
                    maxWidth: 200,
                    showDelay: 0
                });
            }else {
                $ionicLoading.hide();
            }

        };
        $scope.search = function (searchKey) {
            $scope.searchKey = searchKey;
            var latitude = window.localStorage['latitude'];
            var longitude = window.localStorage['longitude'];

            URL.keySearch = "query_type=key&keyword="+searchKey;
            if(latitude!=null && longitude!=null){
                URL.point +="&point="+latitude+","+longitude;
            }else {
                URL.point +="&point=22.5347168,113.971228";//默认坐标,世界之窗
            }

            $scope.loading(true);
            $http.get(URL.toString()).then(function successCallback(response) {
                $scope.loading(false);
                console.log(response);
                $rootScope.ksResults = response.data;
                SearchResult.save("key",response.data);
            },function errorCallback(response) {
                $scope.loading(false);
                if(response.status == 500) $scope.errorPop("没有搜索到结果");
                else $scope.errorPop("未知错误");
            });
            $scope.errorPop = function(message) {
                $scope.data = {};

                // 自定义弹窗
                var myPopup = $ionicPopup.show({
                    template: '<center style="color:red;">'+message+'</center>',
                    title: '错误提示',
                    subTitle: '此窗口3秒后关闭',
                    scope: $scope,
                    /*buttons: [
                        { text: 'Cancel' },
                        {
                            text: '<b>Save</b>',
                            type: 'button-positive',
                            onTap: function(e) {
                                if (!$scope.data.wifi) {
                                    // 不允许用户关闭，除非输入 wifi 密码
                                    e.preventDefault();
                                } else {
                                    return $scope.data.wifi;
                                }
                            }
                        },
                    ]*/
                });
                myPopup.then(function(res) {
                    console.log('Tapped!', res);
                });
                $timeout(function() {
                    myPopup.close(); // 3秒后关闭弹窗
                }, 3000);
            };
        };

        $scope.search($stateParams.searchKey);

        $scope.searchMore = function (moreIndex) {
            if(moreIndex != null){
                //展开更多
                console.log("nowPage:"+$rootScope.ksResults.nowPage+" moreIndex:"+moreIndex);
                $scope.showspinner = true;
                $http.get(URL.toString()+"&page="+moreIndex)
                    .success(function (response) {
                        //新数据合并到前面的数据中
                        var temp = $rootScope.ksResults.sights;
                        $rootScope.ksResults = response;
                        $rootScope.ksResults.sights = temp.concat(response.sights);
                        SearchResult.save("key",$rootScope.ksResults);

                        $scope.showspinner = false;
                    });

            }
        };

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

        $scope.facetview = {
            type : "home",
            title : "筛选"
        };
        //返回到facet
        $scope.backToFacet = function () {
            $scope.facetview.title = "筛选";
            $scope.facetview.hidehome = false;
            $scope.facetview.showsightType = false;
            $scope.facetview.showCompleteButton = false;
            $scope.facetview.showCheckButton = false;
            $scope.facetview.showDistance = false;
            $scope.facetview.showScore = false;
        };
        //筛选景点类型
            $scope.showSightType = function () {
                var fv = $scope.facetview;
                fv.type = "sightType";
                fv.title = "返回";
                fv.hidehome = true ;
                fv.showsightType = true;
                fv.typeUncheck = true;
                fv.typeCheckAll = false;
                fv.showCompleteButton = true;
                fv.showCheckButton = true;

                $scope.sightFacets = $rootScope.ksResults.sightFacetFields;
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
                var items = $rootScope.ksResults.sightFacetFields[0].items;
                var sightType = "";
                for(x in items){
                   if(items[x].checked){
                       sightType += items[x].name + ",";
                   }
                }
                if(sightType != null){
                    sightType = sightType.substring(0,sightType.length - 1); //去掉最后的逗号

                    URL.sightType = "&sight_type="+sightType;
                    $scope.loading(true);
                    $http.get(URL.toString()).then(function successCallback(response) {
                        var tmp = $rootScope.ksResults.sightFacetFields; //保留原有facet
                        $rootScope.ksResults = response.data;
                        $rootScope.ksResults.sightFacetFields = tmp;
                        $scope.loading(false);
                        $scope.catFacetMenu();
                    },function errorCallback(response) {

                    });
                }

            };

        //去除字符串空格
        var trim = function trim(text){
            var str = text.replace(/\s+/g, '');
            return str;
        };
        //距离区间选择
        $scope.showDistanceType = function (commit) {
            if(commit == null){
                var fv = $scope.facetview;
                fv.type = "distance";
                fv.title = "返回";
                fv.hidehome = true ;
                fv.showDistance = true;
                $scope.sightDistance = $rootScope.ksResults.sightFacetDistances;
            }else if (commit == "clear") {
                URL.distance = "";
            }else {
                URL.distance = "&distance="+trim(commit);
                $scope.loading(true);
                $http.get(URL.toString()).then(function successCallback(response) {
                    var tmp = $rootScope.ksResults.sightFacetDistances; //保留原有facet
                    $rootScope.ksResults = response.data;
                    $rootScope.ksResults.sightFacetDistances = tmp;
                    $scope.loading(false);
                    $scope.catFacetMenu();
                },function errorCallback(response) {

                });
            }

        };
        //评分区间选择
        $scope.showScoreType = function (commit) {
            if(commit == null){
                var fv = $scope.facetview;
                fv.type = "score";
                fv.title = "返回";
                fv.hidehome = true ;
                fv.showScore = true;
                $scope.sightScore = $rootScope.ksResults.sightFacetRanges[0].items;
                for( i =0 ;i<$scope.sightScore.length;i++){
                    $scope.sightScore[i].end = parseFloat($scope.sightScore[i].name) + 1.0;
                }
            }else if (commit == "clear") {
                URL.score_range = "";
            }else {
                URL.score_range = "&score_range="+trim(commit);
                $scope.loading(true);
                $http.get(URL.toString()).then(function successCallback(response) {
                    var tmp = $rootScope.ksResults.sightFacetRanges; //保留原有facet
                    $rootScope.ksResults = response.data;
                    $rootScope.ksResults.sightFacetRanges = tmp;
                    $scope.loading(false);
                    $scope.catFacetMenu();
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
                $scope.loading(true);
                $http.get(URL.toString()).then(function successCallback(response) {
                    $rootScope.ksResults = response.data;
                    $scope.loading(false);
                },function errorCallback(response) {

                });
            }



}]);

app.controller('sightDetailCtrl',['$rootScope','$scope','$http','$stateParams','SearchResult','$ionicSlideBoxDelegate',
                '$timeout', '$sce','$location','$cordovaInAppBrowser','$window',
    function ($rootScope,$scope,$http,$stateParams,SearchResult,$ionicSlideBoxDelegate,$timeout,
              $sce,$location,$cordovaInAppBrowser,$window) {

        $scope.myActiveSlide = 1;
        $scope.slideChanged = function(index) {
            $scope.slideIndex = index;
        };
        var results;
        if($stateParams.type == 'near'){
            $scope.sight = SearchResult.getSightById("near",$stateParams.sightId,$rootScope.nsResults.sights);

        }else if ($stateParams.type == 'key'){
            $scope.sight = SearchResult.getSightById("key",$stateParams.sightId,$rootScope.ksResults.sights);
        }

        if($scope.sight.comments != null && $scope.sight.comments.length ==1 && $scope.sight.comments[0].trim() == ""){
            $scope.sight.comments = [];
        }
        if($scope.sight.near_hotels != null && $scope.sight.near_hotels.length ==1 && $scope.sight.near_hotels[0].trim() == ""){
            $scope.sight.near_hotels = [];
        }
        if($scope.sight.tickets != null && $scope.sight.tickets.length ==1 && $scope.sight.tickets[0].trim() == ""){
            $scope.sight.tickets = [];
        }

        $scope.openUrl = function (url) {
            var isIOS = ionic.Platform.isIOS();
            var isAndroid = ionic.Platform.isAndroid();
            console.log("Platform:"+ionic.Platform.platform());

            if(isAndroid || isIOS){

                var options = {
                    location: 'yes',
                    clearcache: 'yes',
                    toolbar: 'yes'
                };

                $cordovaInAppBrowser.open(url, '_blank', options)

            }else {
                $window.open(url);
            }

        };



        var timer = $timeout(
            function() {
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
            }, 1500);


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

app.filter('trustHtml', function ($sce) {
    return function (input) {
        return $sce.trustAsHtml(input);
    }
});

app.filter('offhtml', function () {
    return function (str) {
        if(str != null){
            str = str.replace(/<\/?[^>]*>/g,''); //去除HTML tag
            str = str.replace(/[ | ]*\n/g,'\n'); //去除行尾空白
            //str = str.replace(/\n[\s| | ]*\r/g,'\n'); //去除多余空行
            str=str.replace(/&nbsp;/ig,'');//去掉&nbsp;
        }
        return str;
    }
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

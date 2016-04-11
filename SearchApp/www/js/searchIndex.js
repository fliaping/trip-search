/**
 * Created by Payne on 4/11/16.
 */
var app = angular.module('searchIndex', ['ionic']);

app.controller("indexCtrl",function ($scope,$http,$ionicModal,$timeout,$ionicSideMenuDelegate) {


    //隐藏/打开侧边栏
    $scope.catSearchMenu = function () {
        $ionicSideMenuDelegate.toggleLeft(); //??
    };

    // 创建并载入模板
    $ionicModal.fromTemplateUrl('searchInput.html',function (modal) {
        $scope.searchModal = modal;
    },{
        scope: $scope,
        animation: 'slide-in-up'
    });

    //打开搜索输入页
    $scope.openSearch = function () {
        $scope.searchModal.show();
    };

    //关闭搜索输入页
    $scope.closeSearch = function () {
        $scope.searchModal.hide();
    };

    //执行搜索
    $scope.doSearch = function (search) {
        if(search != null){
            $http.get("http://localhost:9090/ss?query_type=near&distance=10&point=22.5347168,113.971228")
                .success(function (response) {
                    $scope.searchresult = response.sights;
                });

        }else {

        }
    }
});
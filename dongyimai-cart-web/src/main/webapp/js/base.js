//定义模块(不带分页)
var app=angular.module("dongyimai",[]);


//定义过滤器：转换html显示、
app.filter("trustHtml",["$sce",function ($sce) {


    return function (data) {

        return $sce.trustAsHtml(data);
    }



}]);
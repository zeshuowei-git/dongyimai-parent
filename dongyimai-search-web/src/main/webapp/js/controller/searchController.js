app.controller("searchController", function ($scope,$location,searchService) {


    //定义搜索添加对象
    $scope.searchMap = {
        'keywords': '',
        'category': '',
        'brand': '',
        spec: {},
        'price': '',
        'pageNo': 1,
        'pageSize': 20,
        'sort':'',
        'sortField':''
    };

    //加载搜索条件
    $scope.loadKeyWords=function(){


       //获取参数
        var keywords=$location.search()['keywords'];
        if(keywords){
            //赋值
            $scope.searchMap.keywords=keywords;
            //查询
            $scope.search();

        }


    }


    //判断当前搜索条件中是否有品牌列表中的品牌名称
    $scope.keyWordsIsBrand=function(){



       if($scope.resultMap){
           //获取品牌列表
           var brandList=$scope.resultMap.brandList;
           //遍历品牌列表
           for(var i=0;i<brandList.length;i++){

               if($scope.searchMap.keywords.indexOf(brandList[i].text)>=0){

                   return true;

               }




           }

       }



        return false;



    }


    //排序搜索
    $scope.sortSearch=function(sortField,sort){
        //设置排序方式
        $scope.searchMap.sort=sort;
        //设置排序字段
        $scope.searchMap.sortField=sortField;
        //搜索
        $scope.search();
    }


    //是否为当前选中页
    $scope.ispage = function (pageNo) {

        if (parseInt(pageNo) == parseInt($scope.searchMap.pageNo)) {

            return true;
        }

        return false;

    }


    //提交页码
    $scope.queryByPage = function (pageNo) {


        if (pageNo <= 0 || pageNo > $scope.resultMap.totalPages) {

            return;
        }

        //设置当前页
        $scope.searchMap.pageNo = pageNo;
        //发起搜素
        $scope.search();


    }


    //生成分页导航栏
    buildPageLabel = function () {

        //定义存放页码的数组
        $scope.pageList = [];

        //获取最大页
        var maxPage = $scope.resultMap.totalPages;

        //开始页
        var startPage = 1;
        //结束页
        var endpage = maxPage;

        //开始的省略号
        $scope.firstDot = true;
        //结束的省略号
        $scope.lastDot = true;

        //判断总页数是否大于
        if (maxPage > 5) {

            //当前页选择前面
            if ($scope.searchMap.pageNo <= 3) {

                endpage = 5;
                //隐藏前面
                $scope.firstDot = false;

            } else if ($scope.searchMap.pageNo >= maxPage - 2) {
                //当前页选择后面

                startPage = maxPage - 3;
                $scope.lastDot = false;

            } else {
                //当前页选择中间
                startPage = parseInt($scope.searchMap.pageNo) - 1;
                endpage = parseInt($scope.searchMap.pageNo) + 1;
            }

        }


        //循环存储
        for (var i = startPage; i <= endpage; i++) {

            $scope.pageList.push(i);

        }


    }


    //移除搜索项
    $scope.removeSearchItem = function (key) {


        if (key == "category" || key == "brand" || key == "price") {

            $scope.searchMap[key] = "";
        } else {

            delete $scope.searchMap.spec[key];

        }
        //恢复到起始页
        $scope.searchMap.pageNo = 1;
        //发起搜索
        $scope.search();

    }


    //添加搜索选项
    $scope.addSearchItem = function (key, value) {
        //category  ,平板电视
        //brand, 三星
        //规格： key:规格名称 value:规格选项
        if (key == "category" || key == "brand" || key == "price") {
            $scope.searchMap[key] = value;
        } else {
            $scope.searchMap.spec[key] = value;
        }

        //恢复到起始页
        $scope.searchMap.pageNo = 1;
        //发起搜索
        $scope.search();

    }


    //条件对象
    // $scope.searchMap={};
    //搜索
    $scope.search = function () {


        searchService.search($scope.searchMap).success(
                function (response) {


                $scope.resultMap = response;

                //生成导航栏
                buildPageLabel()
            }
        )


    }


})

//父级controller
app.controller("baseController",function($scope){


    //复选框的选择

    //定义数组，存储id
    $scope.selectIds=[];

    //定义选中或者取消id的函数
    $scope.updateSelection=function($event,id){


        if($event.target.checked){

            //存储到数组
            $scope.selectIds.push(id);


        }else{

            //获取指定id的索引
            var index=$scope.selectIds.indexOf(id);

            $scope.selectIds.splice(index,1);
        }



    }

    //分页

    /**
     * 分页组件的配置对象
     * @type {{currentPage: number, totalItems: number, itemsPerPage: number, perPageOptions: number[], onChange: onChange}}
     *
     * currentPage：当前页
     * totalItems: 总记录数
     *itemsPerPage：每页条数
     * perPageOptions：每页条数列表
     * onChange：监听页面的改变
     *
     *
     *
     */
    $scope.paginationConf={

        currentPage: 1,
        totalItems: 10,
        itemsPerPage: 10,
        perPageOptions: [10, 20, 30, 40, 50],

        onChange: function(){


            $scope.reloadList();//重新加载
        }

    }

    //重新加载的函数
    $scope.reloadList=function(){

        //调用findPage
        // $scope.findPage($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);
        $scope.search($scope.paginationConf.currentPage,$scope.paginationConf.itemsPerPage);


    }



})

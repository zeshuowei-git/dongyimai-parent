 //用户表控制层 
app.controller('userController' ,function($scope,$controller   ,userService){	
	
	$controller('baseController',{$scope:$scope});//继承

	//发送验证码
	$scope.sendSmsCode=function(){

		//判断手机号是否为空
		if(!$scope.entity.phone){

			alert("请输入手机号！")

			return ;

		}


		//发送
		userService.sendSmsCode($scope.entity.phone).success(
			function(response){

				if(response.success){

					alert(response.message);

				}else{

					alert(response.message);
				}


			}
		)


	}



	//注册用户
	$scope.reg=function(){


	    //判断两次密码是否一致
        if($scope.entity.password!=$scope.password){

            alert("两次密码不一致，请重新输入！")

            return ;
        }

        //判断验证码不能为空
		if(!$scope.code){

			alert("验证码不能为空！")
			return ;
		}



        userService.add($scope.entity,$scope.code).success(function(response){

        	if(response.success){


        		//发送请求，去登录
				location.href="http://localhost:9106/";

        		alert(response.message)
			}else{

                alert(response.message)
			}


		})


	}



    //保存
    $scope.save=function(){
        var serviceObject;//服务层对象
        if($scope.entity.id!=null){//如果有ID
            serviceObject=userService.update( $scope.entity ); //修改
        }else{
            serviceObject=userService.add( $scope.entity  );//增加
        }
        serviceObject.success(
            function(response){
                if(response.success){
                    //重新查询
                    $scope.reloadList();//重新加载
                }else{
                    alert(response.message);
                }
            }
        );
    }


    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		userService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		userService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		userService.findOne(id).success(
			function(response){
				$scope.entity= response;					
			}
		);				
	}
	

	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		userService.dele( $scope.selectIds ).success(
			function(response){
				if(response.success){
					$scope.reloadList();//刷新列表
					$scope.selectIds=[];
				}						
			}		
		);				
	}
	
	$scope.searchEntity={};//定义搜索对象 
	
	//搜索
	$scope.search=function(page,rows){			
		userService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
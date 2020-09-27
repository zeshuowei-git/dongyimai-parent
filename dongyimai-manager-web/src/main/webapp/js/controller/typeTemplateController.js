 //类型模板控制层 
app.controller('typeTemplateController' ,function($scope,$controller ,brandService ,specificationService ,typeTemplateService){
	
	$controller('baseController',{$scope:$scope});//继承


	//优化数据显示  key=text
	$scope.jsonToString=function(arrString,key){

		//转化成对象
		var arrObj=JSON.parse(arrString);

		var value="";

		//遍历
		for(var i=0;i<arrObj.length;i++){

			 var text=arrObj[i][key];


			 if(i>0){

			 	value+=","
			 }

        	value+=text;


		}


		return value;

	}


    //定义扩展属性的列表
    $scope.entity={customAttributeItems:[]}

    //添加扩展属性行
    $scope.addTableRow=function(){

        $scope.entity.customAttributeItems.push({});

    }
    //移除扩展属性行
    $scope.deleTableRow=function(index){

        $scope.entity.customAttributeItems.splice(index,1);

    }



    //初始化方法
	$scope.selectOptionInit=function(){

		//初始化品牌
		$scope.findBrandList();
		//初始化规格
		$scope.findSpecList();


	}



	//定义select2的静态数据--品牌
	$scope.brandList={data:[{id:'1',text:'华为'},{id:'2',text:'苹果'},{id:'3',text:'中兴'}]}

    /**
	 * 获取品牌选项列表
     */
	$scope.findBrandList=function(){


        brandService.selectOptions().success(

        	function(response){

			$scope.brandList={data:response};

			}

		)

	}


    //定义select2的静态数据--规格
    $scope.specList={data:[{id:'1',text:'尺寸'},{id:'2',text:'网络'},{id:'3',text:'颜色'}]}

    /**
     * 获取品牌选项列表
     */
    $scope.findSpecList=function(){


        specificationService.selectOptions().success(

            function(response){

                $scope.specList={data:response};

            }

        )

    }


	/*
	* select2的使用：
	*   使用步骤:
	*     1.引入select2的相关js
	*     2.准备数据
	*       注意：
	*        数据格式必须按照固定的格式
	*          {data:[{id:'',text:''},{id:'',text:''}]}
	*
	*     3.使用select2
	*
	*	<input select2   select2-model="entity.brandIds" config="brandList" multiple />

	*      select2:声明使用select2
	*      select2-model:和angular的整合标签作用和ng-model类似
	*      config：数据源，下拉列表的待选内容
	*      multiple：可以复选
	*
	*
	* */




    //读取列表数据绑定到表单中  
	$scope.findAll=function(){
		typeTemplateService.findAll().success(
			function(response){
				$scope.list=response;
			}			
		);
	}    
	
	//分页
	$scope.findPage=function(page,rows){			
		typeTemplateService.findPage(page,rows).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
	
	//查询实体 
	$scope.findOne=function(id){				
		typeTemplateService.findOne(id).success(
			function(response){

				$scope.entity= response;

				$scope.entity.specIds=JSON.parse(response.specIds);
				$scope.entity.brandIds=JSON.parse(response.brandIds);
				$scope.entity.customAttributeItems=JSON.parse(response.customAttributeItems);


			}
		);				
	}
	
	//保存 
	$scope.save=function(){				
		var serviceObject;//服务层对象  				
		if($scope.entity.id){//如果有ID
			serviceObject=typeTemplateService.update( $scope.entity ); //修改  
		}else{
			serviceObject=typeTemplateService.add( $scope.entity  );//增加 
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
	
	 
	//批量删除 
	$scope.dele=function(){			
		//获取选中的复选框			
		typeTemplateService.dele( $scope.selectIds ).success(
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
		typeTemplateService.search(page,rows,$scope.searchEntity).success(
			function(response){
				$scope.list=response.rows;	
				$scope.paginationConf.totalItems=response.total;//更新总记录数
			}			
		);
	}
    
});	
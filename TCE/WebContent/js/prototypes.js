$(document).ready(function() {
	
	$('#invio').click(function() {
				//definire i tre prototipi di oggetto, variabile e tipo_primitivo	
					
					var result = {
						    vars: [ 
						        {id:'m1', value:'#apple_1'}, 
						        {id:'m2', value:'#apple_1'},
						        {id:'m3', value:'#apple_2'}
						    ],
						    objects:[  
						        {type:'Apple', id:'apple_1'},
						        {type:'Apple', id:'apple_2'},
						        {type:'Apple', id:'apple_3'}
						    ]
						}
	  drawResult(result);
	});

	//prototipo delle variabili che puntano agli oggetti
	function TCE_ObjectReference(id,value){
		this.id = id;
		this.value = value;
		this.drawVar = function(pos_var,paper){
    var rect = paper.rect(pos_var,30,100,70);
    rect.node.id = this.id;
    rect.attr("fill", "#f00");
    //testo dentro alla variabile
	   paper.text(pos_var+50, 50, ""+this.id).attr({
						"fill" : "white",
					    "font-size" : 20
					});
        return rect;  
		};
   }    
    //TCE_Value x i tipi primitivi
	function TCE_Value(id,value){
		this.id = id;
		this.value = value;
		this.drawValue = function(pos_var,paper){
			//disegna la variabile valore->"ancora da definire"
		};
	}
    //prototipo degli oggetti
    function TCE_Object(type,id){
    	this.type = type;
    	this.id = id;
    	this.draw = function (paper,x,y,scal,rot){
    	//path della mela  
		 mela_back = paper.path("M415.833,134.334c0,0,70.001,59.167,16.668,211.667c0,0-34.165,108.667-123.332,133.333c0,0-18.334,9.166-44.167-4.167c0,0-15.833-14.166-56.667-0.833c0,0-22.5,8.332-50.833-13.334c0,0-125.833-102.5-136.667-215.833c0,0-14.167-92.499,85-128.33c0,0,60.833-19.167,109.167,15.833c0,0-14.999-57.499-38.333-90.832c0,0,25.833-21.667,38.333-1.667c0,0,15.834,20.833,30.833,94.167c0,0,3.333-2.5,25-11.667c0,0,80.833-77.5,204.166-61.666C475.001,61.003,467.498,100.998,415.833,134.334z");
	    var foglia = paper.path("M247,157c0,0,73.333-96,218.667-90c0,0-18,54-85.333,77.334c0,0-58.666,26-132.667,20L247,157z");
	    var gambo = paper.path("M188.334,43c0,0,14-12.667,22,2.667c0,0,40,74.667,26,144c0,0-10,4.667-6-21.333c0,0,4.667-8-10.667-48C219.667,120.333,198.334,58.334,188.334,43z");
	    var mela_front = paper.path("M247,171.667c0,0,89.333,11.333,162.666-34c0,0,52.668,51.333,31.334,152c0,0-20.002,127.998-109.335,173.332c0,0-32.667,17.333-50,8c0,0-29.999-18.665-75.333-3.999c0,0-25.334,6.666-38.667-8.667c0,0-104.667-89.334-132.667-180c0,0-32-88,45.334-142c0,0,65.333-42.667,140.667,7.333c0,0,4.667,16.667,2,32c0,0-1.334,29.333,16.667,18.667C239.666,194.331,247,186.333,247,171.667z");
	    //attributi della mela
	    mela_back.attr("fill","black");
	 	foglia.attr("fill","#006600");
	 	gambo.attr("fill","#660000");
	 	mela_front.attr("fill","#990000");
	 	
	 	 var st = paper.set();
	 	    st.push(mela_back,foglia,gambo,mela_front);
	 	    //st.transform(scal+" "+x+","+y+" "+rot);
	 	    //N.B. da sistemare deve funzionare con i parametri passati ala funzione 
	 	    st.transform(" s0.25,0.25,"+x+","+"360","0");
	 	   mela_back.node.id = this.id;
	 	 
    	};
    }
    
     function TCE_path(){
    	
    	 
    	 this.drawPath = function (canvas,paper,var_svg,value_svg){
    		
    	   var top_canvas = canvas.offset().top;
  		   var left_canvas = canvas.offset().left;
  		   
  		   var var_svg = $(var_svg);
  		   var top_var  = var_svg.offset().top;
  		   var left_var = var_svg.offset().left;
  		   var var_y = top_var - top_canvas;
		   var var_x = left_var - left_canvas;
  		  /*
           var value_svg = $(value_svg);
    	   var top_obj = value_svg.offset().top;
  		   var left_obj = value_svg.offset().left;
  		   var obj_x = top_obj - top_canvas;
  		   var obj_y = left_obj - left_canvas;*/
  		 alert($(value_svg)[0].getBBox().x);
  		   
  		   var obj_x = $(value_svg)[0].getBBox().x;
  		   var obj_y = $(value_svg)[0].getBBox().y;
  		 
  		   var linea = paper.path("M "+var_x+" "+var_y+" L "+obj_x+" , "+obj_y);
    		 
    	 };
     }
     
     function drawResult(result){
    	 
    	 var paper= Raphael("canvas", 500, 500);
 		 //ciclo per le tce_objectReference 
    	 var pos_var= 20;
     for(var i = 0; i < result.vars.length;i++) {
             var objectReference = new TCE_ObjectReference(result.vars[i].id,result.vars[i].value);
                 objectReference.drawVar(pos_var,paper);
                            var pos_var = pos_var+160;
                        	 
                        	   
		   }
     
     
     //ciclo per le tce_object
     var pos_apple= 20;
	  for(var i = 0; i < result.objects.length;i++) {
		  	 if(result.objects[i].type =='Apple'){
		  		 
		  		var tce_Object = new TCE_Object(result.objects[i].type,result.objects[i].id);  
		  		    tce_Object.draw(paper,pos_apple,"360"," s0.25,0.25,","0");
		        var pos_apple = pos_apple+160;
		        
		        
				
		  	 }
		   }
    
	    //terzo ciclo per le linee
	  for(var i = 0; i < result.vars.length;i++) {
		  var  var_svg = "#"+result.vars[i].id; // SVG del rettangolo della variabile
		  var value_svg = result.vars[i].value; // SVG della mela
			// alert(JSON.stringify($(var_svg).offset().left));
	      //top della mela-top canvas, left della mela-left del canvas
		   var canvas = $("#canvas");
		   var top_canvas = canvas.position().top;
  		   var left_canvas = canvas.position().left;
  		   
  		   var var_svg = $(var_svg);
  		   var top_var  = var_svg.position().top;
  		   var left_var = var_svg.position().left;
  		   var var_y = top_var;
		   var var_x = left_var;
  		  /*
           
    	   var top_obj = value_svg.offset().top;
  		   var left_obj = value_svg.offset().left;
  		   var obj_x = top_obj - top_canvas;
  		   var obj_y = left_obj - left_canvas;*/
		   
		   var value_svg = $(value_svg);
		   var obj_x = $(value_svg).position().left;
  		   var obj_y = $(value_svg).position().top;
  		   
		  
  		   var linea = paper.path("M "+var_x+" "+var_y+" L "+obj_x+" , "+obj_y); 
		   
		   //var tce_path = new TCE_path();
		   //tce_path.drawPath(canvas,paper,var_svg,value_svg);
		   //var linea = paper.path("M "+var_x+" "+var_y+" L 200 , 200");
		   //var linea = paper.path("M "+var_x+" "+var_y+" L "+obj_x+" , "+obj_y+"");
		    //linea.animate({path:"M "+var_x+" "+var_y+" L 200,200"},3000);
		 
		}
     
     
     }
});

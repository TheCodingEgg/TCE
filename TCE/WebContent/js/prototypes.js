$(document)
    .ready(
        function() {

          var state = 0;
          $('#run').click(function() {

            drawResult(result, state);
            state++;

          });
          $("#run").mouseup(function() {
            $("svg").remove();
          });
          // prototype of the primitive types
          function TCE_Value(name, value) {
            this.name = name;
            this.value = value;
            this.drawValue = function(pos_varPri, paper) {

              var circle = paper.circle(pos_varPri + 30, 40, 40);

              circle.attr("fill", "#f00");
              circle.attr("stroke", "#fff");
              // text inside of variable primitive
              paper.text(pos_varPri + 30, 40,
                  "" + this.name + " = " + this.value).attr({
                "fill" : "white",
                "font-size" : 20
              });
            };
          }
          

          // prototype of the object variables
          function TCE_ObjectReference(name, value) {
            this.name = name;
            this.value = value;
            this.drawVar = function(pos_var, paper) {
              var rect = paper.rect(pos_var, 100, 100, 70);
              rect.node.id = this.name;
              rect.attr("fill", "#f00");
              // text inside of object variable
              paper.text(pos_var + 50, 130, "" + this.name).attr({
                "fill" : "white",
                "font-size" : 20
              });

            };
          }

          // prototype of the objects
          function TCE_Object(type, id) {
            this.type = type;
            this.id = id;
            this.draw = function(paper, x, y, scal, rot) {
              // path of apple
              mela_back = paper
                  .path("M415.833,134.334c0,0,70.001,59.167,16.668,211.667c0,0-34.165,108.667-123.332,133.333c0,0-18.334,9.166-44.167-4.167c0,0-15.833-14.166-56.667-0.833c0,0-22.5,8.332-50.833-13.334c0,0-125.833-102.5-136.667-215.833c0,0-14.167-92.499,85-128.33c0,0,60.833-19.167,109.167,15.833c0,0-14.999-57.499-38.333-90.832c0,0,25.833-21.667,38.333-1.667c0,0,15.834,20.833,30.833,94.167c0,0,3.333-2.5,25-11.667c0,0,80.833-77.5,204.166-61.666C475.001,61.003,467.498,100.998,415.833,134.334z");
              var foglia = paper
                  .path("M247,157c0,0,73.333-96,218.667-90c0,0-18,54-85.333,77.334c0,0-58.666,26-132.667,20L247,157z");
              var gambo = paper
                  .path("M188.334,43c0,0,14-12.667,22,2.667c0,0,40,74.667,26,144c0,0-10,4.667-6-21.333c0,0,4.667-8-10.667-48C219.667,120.333,198.334,58.334,188.334,43z");
              var mela_front = paper
                  .path("M247,171.667c0,0,89.333,11.333,162.666-34c0,0,52.668,51.333,31.334,152c0,0-20.002,127.998-109.335,173.332c0,0-32.667,17.333-50,8c0,0-29.999-18.665-75.333-3.999c0,0-25.334,6.666-38.667-8.667c0,0-104.667-89.334-132.667-180c0,0-32-88,45.334-142c0,0,65.333-42.667,140.667,7.333c0,0,4.667,16.667,2,32c0,0-1.334,29.333,16.667,18.667C239.666,194.331,247,186.333,247,171.667z");
              // attributes of the apple
              mela_back.attr("fill", "black");
              foglia.attr("fill", "#006600");
              gambo.attr("fill", "#660000");
              mela_front.attr("fill", "#990000");

              var st = paper.set();
              st.push(mela_back, foglia, gambo, mela_front);
              // st.transform(scal+" "+x+","+y+" "+rot);
              // N.B. to fix should work with the parameters passed to the
              // function
              st.transform(" s0.25,0.25," + x + "," + "360", "0");
              mela_back.node.id = this.id;

            };
          }

          // prototype of the paths
          function TCE_path(id) {
            this.id = id;
            this.drawPath = function(var_svg, value_svg, canvas, paper) {

              var top_canvas = canvas.position().top;
              var left_canvas = canvas.position().left;

              var top_var = var_svg.position().top;
              var left_var = var_svg.position().left;
              var var_x = left_var + 50;
              var var_y = top_var + 70;

              var obj_x = $(value_svg).position().left;
              var obj_y = $(value_svg).position().top;
              obj_x = obj_x + 60;
              obj_y = obj_y + 35;

              var linea = paper.path("M " + var_x + " " + var_y + " L " + obj_x
                  + " , " + obj_y);
              // linea.animate({path:"M "+var_x+5+" "+var_y +" L "+ obj_x +" ,
              // "+ obj_y},3000);
              linea.node.id = this.id;

            };
          }

          function drawResult(result, state) {

            var paper = Raphael("canvas", 800, 800);
            state = result[state];
            var pos_varPri = 20;

            // ciclo per le tce_Value
            for ( var i = 0; i < state.varsPrimitive.length; i++) {

              var varsPrimitive = new TCE_Value(state.varsPrimitive[i].name,
                  state.varsPrimitive[i].value);
              varsPrimitive.drawValue(pos_varPri, paper);
              pos_varPri = pos_varPri + 100;
            }

            var pos_var = 20;
            // loop for tce_objectReference
            for ( var i = 0; i < state.vars.length; i++) {

              var objectReference = new TCE_ObjectReference(state.vars[i].name,
                  state.vars[i].value);
              objectReference.drawVar(pos_var, paper);
              pos_var = pos_var + 160;
            }

            // loop for tce_object
            var pos_apple = 20;
            for ( var i = 0; i < state.objects.length; i++) {

              if (state.objects[i].type == 'net.thecodingegg.Apple') {

                var tce_Object = new TCE_Object(state.objects[i].type,
                    state.objects[i].id);
                tce_Object.draw(paper, pos_apple, "360", " s0.25,0.25,", "0");
                pos_apple = pos_apple + 160;

              }

            }

            // loop for paths
            for ( var i = 0; i < state.vars.length; i++) {

              var var_svg = "#" + state.vars[i].name; // SVG of object variable
              var value_svg = state.vars[i].value; // SVG of apple
              var canvas = $("#canvas");
              var_svg = $(var_svg);
              value_svg = $(value_svg);

              var id = i;
              var tce_path = new TCE_path(id);
              tce_path.drawPath(var_svg, value_svg, canvas, paper);
            }
          }

        });

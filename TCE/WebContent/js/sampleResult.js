var result = [
              {lineNumber:1, varsPrimitive:[], vars: [], objects: []},
              {lineNumber:2, varsPrimitive: [{name:'a', value:3}], vars: [], objects: []},
              {lineNumber:3, varsPrimitive: [{name:'a', value:3}], vars: [], objects: []},
              {lineNumber:4, varsPrimitive: [{name:'a', value:3}, {name:'b', value:5}], vars: [], objects: []},
              {lineNumber:5, varsPrimitive: [{name:'a', value:6}, {name:'b', value:5}], vars: [], objects: []},
              {lineNumber:6, varsPrimitive: [{name:'a', value:6}, {name:'b', value:5}], vars: [{name:'apple1', value:"#679"}], objects: [{type:"net.thecodingegg.Apple", id:"679"}]},
              {lineNumber:7, varsPrimitive: [{name:'a', value:6}, {name:'b', value:5}], vars: [{name:'apple1', value:"#679"}, {name:'apple2', value:"#680"}], objects: [{type:"net.thecodingegg.Apple", id:"679"}, {type:"net.thecodingegg.Apple", id:"680"}]},
              {lineNumber:8, varsPrimitive: [{name:'a', value:6}, {name:'b', value:5}], vars: [{name:'apple1', value:"#680"}, {name:'apple2', value:"#680"}], objects: [{type:"net.thecodingegg.Apple", id:"679"}, {type:"net.thecodingegg.Apple", id:"680"}]},
              
              ];
/*
Apple1Test:6:	{apple1=net.thecodingegg.Apple@679, b=5, a=6}
Apple1Test:7:	{apple1=net.thecodingegg.Apple@679, b=5, a=6, apple2=net.thecodingegg.Apple@680}
Apple1Test:8:	{apple1=net.thecodingegg.Apple@680, b=5, a=6, apple2=net.thecodingegg.Apple@680}*/
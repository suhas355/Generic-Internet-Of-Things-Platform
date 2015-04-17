var exports = module.exports = {}

var graphDB = require('./models/graphschema')
exports.clearDb = function(){

	graphDB.remove({},function(err){
		if(err){
			console.log(err);
		}
	});
}

exports.readGraph = function(){

	exports.clearDb();
	var fs = require('fs');
	var data = fs.readFileSync('./graph.xml','utf8');
	var parseString = require('xml2js').parseString;
	var xml = data;
	var obj;
	parseString(xml, function (err, result) {
		json = JSON.stringify(result);
		obj = JSON.parse(json);
	});
	var routes = obj["Graph"];
	var routeArr = routes["Route"];
	var len = routeArr.length;

	for(var i=0; i<len; i++){

		var output = {};
		var route = routeArr[i];
		var destination = route["Destination"][0];
		output["destination"] = destination;
		var source = route["Source"][0];
		output["source"] = source;
		var pathArr = route["Path"];
		var outputEdges = [];
		for(var j=0; j<pathArr.length; j++){

			var node = pathArr[j]["Node"][0];
			var json = {};
			json[node] = pathArr[j]["Edges"][0];
			outputEdges.push(json);
		}

		output["edges"] = outputEdges;
		var graphData = new graphDB(output);
		graphData.save(graphData, function(err, data){

			if(err){
				console.log("Error");
			} else{
				console.log(data);
			}
		});

	}


}

exports.findNextLocation = function(heatMap, res, source){

	console.log("In findNextLocation")
	var exec = require('child_process').exec,
    child;
    var input = "";
	for(var i=0; i<heatMap.length; i++){
		input = input + heatMap[i] + " ";
	}
	input = input + source + " ";
	console.log(input);
	child = exec('./a.out',
	  function (error, stdout, stderr) {
    
    		var output = {};
    		var locations = stdout.split("\n");
    		output['location'] = locations[0];
    		console.log('Next Location for Driver' + locations[0]);
    		res.send(output);
    		console.log('stderr: ' + stderr);
    		if (error !== null) {
      			console.log('exec error: ' + error);
    		}
	  });

	
	child.stdin.write(input);
	child.stdin.end();
}

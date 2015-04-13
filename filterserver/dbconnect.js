var exports = module.exports = {};

var devicedb = require('./models/dbschema');


exports.insertsensordata = function(data) {

	

	/*{"gid":"1","mac":"84:51:BF:2C:8C:03","data":[{"sensorid":"1","type":"temperature"
	,"unit":"celsius","location":"PG1Labs","geo":[20.4,10.2],
	"data":"12"},{"sensorid":"2","type":"Sound","unit":"decibel","location":"Pg1Labs","geolocation"
	:{"longitude":"20.4","latitude":"10.2"},"data":"7"}]}*/

	/*
	{"gid":"1","mac":"84:51:BF:2C:8C:03","data":[{"sensorId":"1","type":"temperature","unit":"celsius","location":"PG1Labs","geo":[20.4,10.2],"data":"-1"},{"sensorId":"2","type":"Sound","unit":"decibel","location":"Pg1Labs","geo":[20.4,10.2],"data":"-1"}]}

	*/

	var jsonData = JSON.parse(data.toString());
	var sensorArr = jsonData['data'];
	var len = sensorArr.length;
	for(var i=0; i<len; i++){
		console.log(sensorArr[i]['sensorId']);
		var sdata = new devicedb(sensorArr[i]);
		console.log(sdata);
		sdata.update( { sensorId : sensorArr[i]['sensorId']},sensorArr[i] , { upsert : true },
			function(err) {
		   		if (err) {
		      		//return res.send(err);
		      		console.log('data error');
		    	}else{
		 	    	console.log('data added'); 
		    	}
		});
	}	
}


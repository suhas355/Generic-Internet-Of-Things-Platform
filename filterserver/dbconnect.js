var exports = module.exports = {};

var devicedb = require('./models/dbschema');


exports.insertsensordata = function(data) {

	var jsonData = JSON.parse(data.toString());
	var sensorArr = jsonData['data'];
	//console.log(sensorArr);
	var len = sensorArr.length;
	for(var i=0; i<len; i++){
		//onsole.log(sensorArr[i]['sensorId']);
		var sdata = new devicedb(sensorArr[i]);
		var sid = sensorArr[i]['sensorId']
		devicedb.update( {"sensorId" : sid},sensorArr[i] , { upsert : true },
			function(err, result) {
		   		if (err) {
		      		console.log('data error');
		    	}
		
		});
	}	
}


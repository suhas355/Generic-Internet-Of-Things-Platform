var exports = module.exports = {};

var devicedb = require('./models/dbschema');


exports.insertsensordata = function(data) {

	/*var fs = require('fs');
	var data = fs.readFileSync('./data.xml','utf8');

	var parseString = require('xml2js').parseString;
	var xml = data;
	var obj;
	parseString(xml, function (err, result) {
		json = JSON.stringify(result);
		obj = JSON.parse(json);
	});
*/
	console.log(data);
/*
	var sensorArr = obj['sensor']['sensordata'];
	var len = sensorArr.length;
	for(var i=0; i<len; i++){
		console.log(sensorArr[i]['sensorId']);
		devicedb.update( { sensorId : sensorArr[i].sensorId  },sensorArr[i] , { upsert : true },
			function(err) {
		   		if (err) {
		      		//return res.send(err);
		      		console.log('data error');
		    	}
		 	    console.log('data added'); 
		    
		});
	}	*/
}


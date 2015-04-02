var exports = module.exports = {};

var MongoClient = require('mongodb').MongoClient;

exports.getSensorList = function(gatewayId, res) {

	MongoClient.connect("mongodb://localhost:27017/sensor", function(err, db) {

		db.collection('test', function(err, collection){

		});
		var collection = db.collection('test');

		collection.find().toArray(function(err, items) {

			
			var sensorArr = items[0].Gateways.Gateway;
			var len = sensorArr.length;
			//console.log(sensorArr);
			for(var i=0; i<len; i++){

				if(sensorArr[i].HWID == (gatewayId)){
					res.send(sensorArr[i]);
					return;
				}
			}
			res.send("Gateway Id not found!!");
		});
	});

}


exports.readXMLFile = function() {

	var fs = require('fs');
	var data = fs.readFileSync('../repository.xml','utf8');

	var parseString = require('xml2js').parseString;
	var xml = data;
	var obj;
	parseString(xml, function (err, result) {
	json = JSON.stringify(result);
	obj = JSON.parse(json);
	});
	var MongoClient = require('mongodb').MongoClient;
	MongoClient.connect("mongodb://localhost:27017/sensor", function(err, db) {

		db.collection('test', function(err, collection) {
			collection.remove();
		});
		var collection = db.collection('test');
		collection.insert(obj);
		db.close();
	});
};

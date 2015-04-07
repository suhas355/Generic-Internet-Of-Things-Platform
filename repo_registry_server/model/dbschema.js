var mongoose = require('mongoose');
var Schema = mongoose.Schema;


var sensorInfoSchema = new Schema({
	sensorId: String,
	gatewayId: String,
  	type: String,
  	unit: String,
  	location : String,
  	geolocation: [{latitude : Number, longitude : Number}],
  	pullfrequency: Number,
  	protocol: String
    });

var sensorinfo = mongoose.model('sensorinfo', sensorInfoSchema);

var gatewayInfoSchema = new Schema({
	gatewayId: String,
	gatewayName: String,
	location : String,
  	geolocation: [{latitude : Number, longitude : Number}]

});

var gatewayinfo = mongoose.model('gatewayinfo', gatewayInfoSchema);

module.exports = {
  sensorinfo : sensorinfo,
  gatewayinfo : gatewayinfo
};
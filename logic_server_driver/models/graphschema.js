var mongoose=require('mongoose');
var Schema=mongoose.Schema;

var graphSchema = new Schema({

	destination: {type:String, required:true},
	source: {type:String, required:true},
	edges:[]
});

module.exports = mongoose.model('routes', graphSchema); 
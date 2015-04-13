var mongoose=require('mongoose');
var Schema=mongoose.Schema;
 
var sensorDataSchema = new Schema({
  sensorId: {type:String, required:true, unique:true},
  type: String,
  unit: String,
  location : String,
  geo: { type: [Number], index: '2d' },
  data: {type: Number, required: true},
  timestamp: { type : Date, default: Date.now }
});
 
module.exports = mongoose.model('filterapi', sensorDataSchema); 
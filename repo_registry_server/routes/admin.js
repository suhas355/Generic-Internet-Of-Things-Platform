var exports = module.exports = {};

var info = require('../model/dbschema');

var express = require('express');
var router = express.Router();

router.route('/insertSensor').post(function(req,res){
  console.log("data from admin********************************");	
  console.log(req.body);

  var sensordata = new info.sensorinfo(req.body);
  console.log("here");
  sensordata.save(function(err){
  	if(err){
  		console.log(err);
  		res.send("error");
  	}else{
  		console.log('Successfully entererd sensor data');
    	res.send('ok');
  	}	
  });
  	
});

router.route('/').get(function(req,res){
  res.sendfile('./public/html/index.html');
});


module.exports = router;
var exports = module.exports = {};

var info = require('../model/dbschema');
var express = require('express');
var router = express.Router();

//get sensor id based on location
router.route('/getsensors/location').post(function(req, res) {
	console.log(req.body.location);
  if(req.body.location == undefined){
    res.status(422).send({error:"Missing mandatory fields in JSON"});
  }else{
      
        info.sensorinfo.find({
              "location": req.body.location
            },
            function(err, sensordata) {
            if (err) {
              return res.send(err);
            }
               res.json(sensordata);
           
           }); 
            
            
    }
});

router.route('/getsensors').post(function(req, res) {
  console.log("Received request query" + JSON.stringify(req.body));
  if(req.body.type == undefined){
    res.status(422).send({error:"Missing mandatory fields in JSON"});
  }else{
      
       var query =  info.sensorinfo.find()
       .where('type').in(req.body['type'])
       .where('location').equals(req.body.location);
       query.exec(function(err, sensordata) {
            if (err) {
              return res.send(err);
            }
               res.json(sensordata);
           
           }); 
            
            
    }
});

//get sensor id based on geo location

router.route('/getsensors/geolocation').post(function(req,res){
    console.log(req.body.location);
    var lat = req.body.latitude;
    var longi = req.body.longitude;
    var radius = req.body.radius;
    if(req.body.altitude == undefined){
	    info.sensorinfo.find( { geo: { $within: { $centerSphere: [ [ req.body.longitude,req.body.latitude ] , req.body.radius / 3963.192 ] } } }, function(err, docs){

	    	if(err){
	    		console.log(err);
	    	} else{
	    		res.send(docs);
	    	}

	    });
	} else{

		info.sensorinfo.find( { geo: { $within: { $centerSphere: [ [ req.body.longitude,req.body.latitude ] , req.body.radius / 3963.192 ] } }, altitude:req.body.altitude }, function(err, docs){

	    	if(err){
	    		console.log(err);
	    	} else{
	    		res.send(docs);
	    	}

	    });

	}
    
});

//get sensor id based on type

module.exports = router;
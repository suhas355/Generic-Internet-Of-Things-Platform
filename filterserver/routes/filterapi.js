var exports = module.exports = {};
var queryMapping = {};
var callbackId =0;
var filterapi = require('../models/dbschema');
var express = require('express');
var router = express.Router();
var Validate = require('../processcb.js');

//Get request for immediate data using sensor id.
router.route('/').get(function(req, res) {
	console.log(req.query);
  filterapi.find(
  	{sensorId : req.query.sensorId},
  	function(err, sensordata) {
    if (err) {
      return res.send("error in request "+err);
    }
 
    res.json(sensordata);
  });
});

//Callback using sensor id
router.route('/registercallback/sensors').post(function(req, res) {
  console.log(req.body);
  
  var reqQuery = req.body;

  if(reqQuery['sensorId'] == undefined)
  {
    console.log("Invalid request ");
    res.status(422).send({error:"Missing mandatory fields in JSON"});

  }else{

  	  var ipAddr = req.headers["x-forwarded-for"]; 
      	if (ipAddr){ 
        	var list = ipAddr.split(","); 
        	ipAddr = list[list.length-1]; 
      	}	 
     	else { 
        	ipAddr = req.connection.remoteAddress; 
      	}

      callbackId++;
      var query = Validate.getQuery(req.body);
     
      queryMapping['c'+callbackId] = {"query":query, "IP":ipAddr};
      if(req.body.tillWhen!=undefined){
        queryMapping['c'+callbackId]["time"] = req.body.tillWhen;
      }
      var ret = {};
      ret['message'] = "Callback registered";
      ret['id'] = 'c'+callbackId;
      res.json(ret);
    }
});

//Immediate data using latitude longitude
router.route('/geolocation').post(function(req, res) {
  console.log(req.body.latitude);
  console.log(req.body.longitude);
  console.log("geolocation called")
  var type = req.body.type;
  filterapi.find( { geo: { $within: { $centerSphere: [ [ req.body.longitude,req.body.latitude ] , req.body.radius / 3963.192 ] } } }, function(err, docs){

  	if(err){
  		console.log(err);
  	} else{
  		var i=0, len=docs.length;
  		var sensorId = [];
  		for(i=0; i<len; i++){
  			sensorId.push(docs[i]['sensorId']);
  		}
  		var query = filterapi.find().where('sensorId').in(sensorId);
  		if(type!=undefined){
  			query = query.where('type').in(type);
  		}
  		query.exec(function(err, sensordata){
  			if(err){
  				res.status(422).send({"Error":"Unable to process"});
  			} else{
          if(sensordata.length == 0){
            res.send({'Message':'No data found'});
          }else{
            console.log("Geo location----- "+JSON.stringify(sensordata));
    				res.send(sensordata);
          }
  			}
  		});
 	 }
  });
   
});


//Immediate data using text-based location
router.route('/location').post(function(req, res) {
  console.log(req.body.location);
  if(req.body.location == undefined){
    res.status(422).send({error:"Missing mandatory fields in JSON"});
  }else{
      if(req.body.type == undefined){
          filterapi.find({
              "location": req.body.location
            },
            function(err, sensordata) {
            if (err) {
              return res.send(err);
            }
               res.json(sensordata);
           
           });  
         } 
      else{
          
         var query =  filterapi.find().where('type').in(req.body.type).where('location').equals(req.body.location);
         query.exec(function(err,sensordata){

              if(err) {
                return res.send(err);
              }
               res.json(sensordata);
            
         });
      }
    }
});

//Callback using lat,long
router.route('/registercallback/geolocation').post(function(req, res) {
  
  console.log("register callback geolocation called")
  var query = Validate.getQuery(req.body);
  var type = req.body.type;
  callbackId++;
  //Get all sensor ids from sensor info table
  filterapi.find({geo: { $nearSphere: [req.body.longitude, req.body.latitude], $maxDistance:req.body.radius}}, function(err, docs){

  	if(err){
  		console.log(err);
  	} else{
  		var i=0, len=docs.length;
  		var sensorId = [];
  		for(i=0; i<len; i++){
  			sensorId.push(docs[i]['sensorId']);
  		}

  		query = query.where('sensorId').in(sensorId);
  		if(type!=undefined){
  			query = query.where('type').in(type);
  		}
  		
  		var ipAddr = req.headers["x-forwarded-for"]; 
      	if (ipAddr){ 
        	var list = ipAddr.split(","); 
        	ipAddr = list[list.length-1]; 
      	}	 
     	else { 
        	ipAddr = req.connection.remoteAddress; 
      	}
      	queryMapping[callbackId] = {"query":query, "IP":ipAddr};
        if(req.body.tillWhen!=undefined){
          queryMapping[callbackId]["time"] = req.body.tillWhen;
        }
      	var ret = {};
      	ret['message'] = "Callback registered";
      	ret['id'] = callbackId;
      	res.json(ret);

 	 }
  });
   
});

//Callback using text-location
router.route('/registercallback/location').post(function(req, res) {
  
  if(req.body.location == undefined){
    res.status(422).send({error:"Missing mandatory fields in JSON"});
  }
  else{

  	  callbackId++;
  	  var location = req.body.location;
   	  var type = req.body.type;
   	  var query = Validate.getQuery(req.body);
   	  if(type!=undefined){
   	  	query = query.where('type').in(type);
   	  }

   	 	var ipAddr = req.headers["x-forwarded-for"]; 
	  	if (ipAddr){ 
	    	var list = ipAddr.split(","); 
	    	ipAddr = list[list.length-1]; 
	  	}	 
	 	else { 
	    	ipAddr = req.connection.remoteAddress; 
	  	}
	  	queryMapping[callbackId] = {"query":query, "IP":ipAddr};
	  	var ret = {};
	  	ret['message'] = "Callback registered";
	  	ret['id'] = callbackId;
	  	res.json(ret);
   }
});

//Callback to send data with certain interval
router.route('/freqdata').post(function(req,res){

    callbackId++;
    var query = Validate.getQuery(req.body);
    var freq = req.body.frequency;
    var ipAddr = req.headers["x-forwarded-for"]; 
    var endTime = req.body.tillWhen;
    if (ipAddr){ 
      var list = ipAddr.split(","); 
      ipAddr = list[list.length-1]; 
    } 
    else { 
      ipAddr = req.connection.remoteAddress; 
    }
    var ret = setInterval(Validate.freqDataCall, freq*1000, ipAddr, query, 'f'+callbackId, endTime);

    Validate.freqDataInterval['f'+callbackId] = ret;
    res.send({"Message":"Callback registered successfully","id":'f'+callbackId});

});


//Test API for adding sensor data..
router.route('/sensordata').post(function(req, res) {
  console.log(req.body);
  var sensordata = new filterapi(req.body);
 
  filterapi.update({sensorId:req.body.sensorId},req.body,{upsert:true},function(err) {
    if (err) {
      return res.send(err);
    }
 
    res.send({ message: 'Data Added' });
  });
});



exports.router = router;
exports.queryMapping = queryMapping;
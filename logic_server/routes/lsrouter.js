var exports = module.exports = {};

var lssoc = require('../lssocket');

var ip = "localhost";

var express = require('express');
var router = express.Router();
var https = require('https');
var cbIds = [];

var requestify = require('requestify');
router.route('/').post(function(req, res) {
	
	console.log("Recv request fr");
	requestify.post('http://'+ip+':5000/getdata/registercallback/sensors', req.body)
		.then(function(response) {
				console.log("Response from filter server " + response.getBody()); 
		    	cbIds.push(response.getBody()['id']);
		    	res.send(response.getBody());
		   
	},
	function(err){
		if(err){
			console.log('Error ' + err);
			res.status(err.getCode()).send(err.getBody());
		}
	});
    
	
});

router.route('/getresults').get(function(req, res){
	console.log('Get result for callback with id ' + req.query.id 
		+ ' from application ip ' + req.connection.remoteAddress);
	var respArr = lssoc.cb_resp;
	var flag = false;
	var cbIndex = -1;

	for(var i=0;i<cbIds.length; i++){
		if(cbIds[i] == req.query.id){
			cbIndex = i;
			break;
		}
	}
	console.log(" cbIndex "+ cbIndex);
	if(cbIndex == -1){
		res.status(400).send({error:"Requested callback id does not exists"});
	}else{

		for(var i=0;i<respArr.length;i++){

			console.log(respArr[i]);
			if(respArr[i][req.query.id] != undefined){
				flag = true;
				var data = respArr[i];
				respArr.splice(i,1);
				cbIds.splice(cbIndex,1);
				res.send(data);

			}
		}
		if(!flag){
			res.send({"id":req.query.id, "Message":"Pending"});
		}
	}
})

exports.router = router;
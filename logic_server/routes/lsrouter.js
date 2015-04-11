var exports = module.exports = {};
var ip = "localhost";

var express = require('express');
var router = express.Router();
var https = require('https');

var requestify = require('requestify');
router.route('/').post(function(req, res) {
	
	requestify.post('http://'+ip+':5000/getdata/registercallback/sensors', req.body)
		.then(function(response) {
    	// Get the response body (JSON parsed or jQuery object for XMLs)
    	console.log("Response from filter server " + response.getBody()); 
    	res.send(response.getBody());
	});
    
	
});

router.route('/getresults').get(function(req, res){
	console.log('Get result for callback with id ' + req.body.id 
		+ ' from application ip ' + req.connection.address);



	var callbackId = req.query.cid;
	
})

exports.router = router;
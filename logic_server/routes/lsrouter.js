var exports = module.exports = {};

var express = require('express');
var router = express.Router();
var https = require('https');


router.route('/').post(function(req, res) {
	
	var jsonObject = JSON.stringify({
    "message" : "The web of things is approaching, let do some tests to be ready!",
    "name" : "Test message posted with node.js",
    "caption" : "Some tests with node.js",
    "link" : "http://www.youscada.com",
    "description" : "this is a description",
    "picture" : "http://youscada.com/wp-content/uploads/2012/05/logo2.png",
    "actions" : [ {
        "name" : "youSCADA",
        "link" : "http://www.youscada.com"
    } ]
	});
 

	var postheaders = {
	    'Content-Type' : 'application/json',
	    'Content-Length' : Buffer.byteLength(jsonObject, 'utf8')
	};
	 
	// the post options
	var optionspost = {
	    host : 'localhost',
	    port : 5000,
	    path : '/getdata/registercallback/sensors',
	    method : 'POST',
	    headers : postheaders
	};

		var reqPost = https.request(optionspost, function(res) {
	    	console.log("statusCode: ", res.statusCode);
	 
		    res.on('data', function(d) {
		        console.info('POST result:\n');
		        process.stdout.write(d);
		        console.info('\n\nPOST completed');
		    });
		});
	 	//var buf = new Buffer(JSON.stringify(req.body));
	 	reqPost.write(jsonObject);
		
		reqPost.on('error', function(e) {
		    console.error(e);
		});
		reqPost.end();
		//res.send("hola");
});

exports.router = router;
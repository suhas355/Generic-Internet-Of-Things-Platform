var exports = module.exports = {};
var app = require('http').createServer();
var io = require('socket.io').listen(app);

var cb_resp = [];

app.listen(3550);

console.log('server listening on localhost:3550');

io.sockets.on('connection', function(socket) {
  
  console.log("----------recved connn-----------");
  socket.on('Callback Response', function(data){
    console.log('message: ' + JSON.stringify(data));
    cb_resp.push(data);
  });

});

exports.cb_resp = cb_resp;
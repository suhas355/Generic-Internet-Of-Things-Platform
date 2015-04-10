var app = require('http').createServer(handler),
  io = require('socket.io').listen(app);

app.listen(3550);

console.log('server listening on localhost:3550');

io.sockets.on('connection', function(socket) {
  
  socket.on('Callback Response', function(data){
    console.log('message: ' + data);
  });

});
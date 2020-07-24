var stompClient = null;

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#greetings").html("");
}

function connect() {
    var socket = new SockJS('/gs-guide-websocket');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function (frame) {
        setConnected(true);
        console.log('Connected: ' + frame);
        stompClient.subscribe('/topic/greetings', function (greeting) {
            showGreeting(JSON.parse(greeting.body).content);
        });
    });
    $("#confirmationModal .close").click();
}

function disconnect() {
    if (stompClient !== null) {
        stompClient.disconnect();
    }
    setConnected(false);
    console.log("Disconnected");
}

function sendName() {
    stompClient.send("/app/hello", {}, JSON.stringify({'name': $("#name").val()}));
}

function showGreeting(message) {
    $("#greetings").append("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });
    $( "#connect" ).click(function() { connect(); });
    $( "#disconnect" ).click(function() { disconnect(); });
    $( "#send" ).click(function() { sendName(); });
    
    $( "#searchbutton" ).click(function() {
    	$("#cancelsearch").css('display','block');
    	$("#searchbutton").css('display','none');
    	searchData($("#search").val()); 
    });


    $( "#cancelsearch" ).click(function() {
    	$("#cancelsearch").css('display','none');
    	$("#searchbutton").css('display','block');
    	eraseSearch(); 
    });


    $('#search').keypress(function(event) {
    	var keycode = (event.keyCode ? event.keyCode : event.which);
        if(keycode == '13') {
        	$("#cancelsearch").css('display','block');
        	$("#searchbutton").css('display','none');
        	searchData($(this).val());  
        }
    });
});

  

function searchData(value) {  
    $('#greetings tr').each(function() {
         var found = 'false';  
         $(this).each(function(){  
              if($(this).text().toLowerCase().indexOf(value.toLowerCase()) >= 0) {  
                   found = 'true';  
              }  
         });  
         if(found == 'true') {  
              $(this).show();  
         }  
         else {  
              $(this).hide();  
         }  
    });  
}

function eraseSearch() {
    $('#greetings tr').each(function() {
         var found = 'false';  
         $(this).each(function(){  
        	 $(this).show();  
         });  
    });  
}
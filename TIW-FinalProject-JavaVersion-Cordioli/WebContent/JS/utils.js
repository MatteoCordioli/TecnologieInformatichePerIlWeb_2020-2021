	function makeCall(method, url, formElement, cback, reset = true) {
	    var req = new XMLHttpRequest(); // visible by closure
	    req.onreadystatechange = function() {
	      cback(req)
	    }; // closure
	    req.open(method, url);
	    if (formElement == null) {
	      req.send();
	    } else {
	      var form=new FormData(formElement)
	      req.send(form);
	    }
	    if (formElement !== null && reset === true) {
	      formElement.reset();
	    }
	  }
(function(){
	document.getElementById("HomePageButton").addEventListener("click", (e)=>{
  		window.location.href = "home.html";
  	});
  	
  	document.getElementById("LogoutButton").addEventListener("click", (e)=>{
  		makeCall("GET", 'Logout', null,
	        function(req) {
	          if (req.readyState == XMLHttpRequest.DONE) {
	            switch (req.status) {
	              case 200:
	            	sessionStorage.clear();
	                break;
	            }
	            
	          }
	        }
	      );
  		window.location.href = "index.html";
  		
  	});
})();
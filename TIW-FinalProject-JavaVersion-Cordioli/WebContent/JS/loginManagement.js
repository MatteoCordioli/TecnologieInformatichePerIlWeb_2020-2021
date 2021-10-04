/**
 * Login management
 */

(function() { // avoid variables ending up in the global scope

  document.getElementById("LogInButton").addEventListener('click', (e) => {
    var form = document.getElementById("LogInForm");
    if (form.checkValidity()) {
      makeCall("POST", 'LogIn', document.getElementById("LogInForm"),
        function(req) {
          if (req.readyState == XMLHttpRequest.DONE) {
            var message = req.responseText;
            switch (req.status) {
              case 200:
            	sessionStorage.setItem('nameUser', message);
                window.location.href = "home.html";
                break;
              case 400: // bad request
                document.getElementById("LogInNotify").textContent = message;
                break;
              case 401: // unauthorized
                  document.getElementById("LogInNotify").textContent = message;
                  break;
              case 500: // server error
            	document.getElementById("LogInNotify").textContent = message;
                break;
            }
          }
        }
      );
    } else {
    	 form.reportValidity();
    }
  });

  document.getElementById("RegisterButton").addEventListener('click', (e) => {
    var form = document.getElementById("RegistrationForm");
 	
    if (form.checkValidity()) {
      makeCall("POST", 'CreateUser', document.getElementById("RegistrationForm"),
        function(req) {
          if (req.readyState == XMLHttpRequest.DONE) {
            var message = req.responseText;
            switch (req.status) {
              case 200:
              	document.getElementById("LogInNotify").textContent = "Utente creato con Successo!";
            	setTimeout(function(){document.getElementById('LogInNotify').textContent="";},3000);
                //window.location.href = "home.html";
                break;
              case 400: // bad request
                document.getElementById("LogInNotify").textContent = message;
                break;
              case 401: // unauthorized
                  document.getElementById("LogInNotify").textContent = message;
                  break;
              case 500: // server error
            	document.getElementById("LogInNotify").textContent = message;
                break;
            }
          }
        }
      );
    } else {
    	 form.reportValidity();
    }
  });

})();
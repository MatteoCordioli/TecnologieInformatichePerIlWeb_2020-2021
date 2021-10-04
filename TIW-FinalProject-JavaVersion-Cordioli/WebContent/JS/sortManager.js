(function(){

	var songList;
	var pageOrchestrator = new PageOrchestrator();
	window.addEventListener("load", () => {
        if (sessionStorage.getItem("idPlaylist") == null || sessionStorage.getItem("namePlaylist") == null) {
          window.location.href = "home.html";
        } else {
          pageOrchestrator.start();
        }
      }, false);


	function PageOrchestrator(){
		this.start=function(){
			songList= new SongList(document.getElementById("songListContainer"));
			document.getElementById("sortNotify").textContent="";
			document.getElementById("namePlaylist").textContent=sessionStorage.getItem("namePlaylist");
			songList.show();
		}
	}

	function SongList(_container){
		this.container=_container;
		this.songs=null;
		this.show=function(){
			var self = this;
	        makeCall("GET", "GetPlaylistById?idPlaylist="+sessionStorage.getItem("idPlaylist"), null,
	        function(req) {
	            if (req.readyState == XMLHttpRequest.DONE) {
		            var message = req.responseText;
		            if (req.status == 200) {
		                var songs = JSON.parse(req.responseText);
		                self.songs=songs;
		                if (songs.length == 0) {
		                	document.getElementById('sortNotify').style.visibility='visible';
		        	        document.getElementById("sortNotify").textContent = "Non dovresti essere in questa pagina!";
		                    return;
		                }
		                self.update(songs);
	            	}
	            	else {
		                document.getElementById('sortNotify').style.visibility='visible';
			        	document.getElementById("sortNotify").textContent = req.responseText;
	            	}
	            } 
	        }
	        );
		};

		this.update = function(arraySongs){
    		var li, anchor, linkText;
    		this.container.innerHTML = ""; // empty the list body
    		var self=this;
    		this.elementDrag=null;
    		for(var i=0;i<arraySongs.length;i++){
    			li = document.createElement("li");
    			anchor=document.createElement("a");
    			li.appendChild(anchor);
    			linkText=document.createTextNode(arraySongs[i].title);
    			anchor.appendChild(linkText);
    			li.setAttribute("idSong", arraySongs[i].idSong);
    			//li.setAttribute("customOrder", i+1);
    			li.className="draggable";
    			anchor.href="#"
    			self.container.appendChild(li);
    		}
    		
    		var elements = document.getElementsByClassName("draggable")
		    for (var i = 0; i < elements.length; i++) {
		    	elements[i].draggable = true;
		        elements[i].addEventListener("dragstart", (e)=>{
		        	songList.elementDrag=e.target.closest("li");
		        }); //save dragged element reference
		        elements[i].addEventListener("dragover", (e)=>{
		        	event.preventDefault(); 
		        	//colorare gli over 
		        }); // change color of reference element to red
		        elements[i].addEventListener("dragleave", (e)=>{
		        	//rimettere i colori a posto dopo averli cabianti nell'hover
		        }); // change color of reference element to black
		        elements[i].addEventListener("drop", (e)=>{
		        	var dest= e.target.closest("li");
		        	var list = Array.from(songList.container.querySelectorAll("li"));
		        	var indexDest=list.indexOf(dest);
		        	var indexStart=list.indexOf(songList.elementDrag);
		        	if(indexStart<indexDest){
		           		songList.elementDrag.parentElement.insertBefore(songList.elementDrag, list[indexDest + 1]);
		        	}
       				else{
            			songList.elementDrag.parentElement.insertBefore(songList.elementDrag, list[indexDest]);
       				}
		        }); //change position of dragged element using the referenced element 
		    } 
    	};

	}

	//obj i'll send to servlet
	function SongOrder(_songId, _songOrder){
		this.songId=_songId;
		this.songOrder=_songOrder;
	}

	document.getElementById("saveOrder").addEventListener('click', (e) => {
		//i create an array of SongOrder to send
		var order= new Array();
		var list = Array.from(songList.container.querySelectorAll("li"));
		for(var i=0; i<list.length; i++){
			var songOrder=new SongOrder(list[i].getAttribute("idSong"), i+1);
			order.push(songOrder);
		}
		
		var stringaJson=JSON.stringify(order);
		document.getElementById("json").value=stringaJson;
		makeCall("POST", 'ModifyOrderSongs?idPlaylist='+sessionStorage.getItem("idPlaylist"), document.getElementById("orderForm"),
	        function(req) {
	          if (req.readyState == XMLHttpRequest.DONE) {
	            var message = req.responseText;
	            document.getElementById('sortNotify').style.visibility='visible';
	            switch (req.status) {
	              case 200:
	            	document.getElementById("sortNotify").textContent = "Ordinamento salvato!";
	                break;
	              case 400: // bad request
	                document.getElementById("sortNotify").textContent = message;
	                break;
	              case 401: // unauthorized
	                  document.getElementById("sortNotify").textContent = message;
	                  break;
	              case 500: // server error
	            	document.getElementById("sortNotify").textContent = message;
	                break;
	            }
	            setTimeout(function(){document.getElementById('sortNotify').style.visibility='hidden';},3000);
	          }
	        }
	      );
    
	});

})();
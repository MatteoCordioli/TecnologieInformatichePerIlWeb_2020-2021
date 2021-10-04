(function (){
	
	var pageOrchestrator = new PageOrchestrator(); // main controller
	var playlistList, songsList, songManager;
	window.addEventListener("load", () => {
        if (sessionStorage.getItem("nameUser") == null) {
          window.location.href = "index.html";
        } else {
          pageOrchestrator.start(); // initialize the components

          pageOrchestrator.refresh();
        } // display initial content
      }, false);


	function PersonalMessage(_username, messagecontainer) {
    	this.username = _username;
        this.show = function() {
          messagecontainer.textContent = this.username;
        }
    }

	function PageOrchestrator() {
        var alertContainer = document.getElementById("id_alert");
        this.start = function() {
        	document.getElementById("albumNotify").textContent = "";
        	document.getElementById("songNotify").textContent = "";
        	document.getElementById("songNotInPlaylistNotify").textContent = "";
        	document.getElementById("playlistNotify").textContent="";
        	personalMessage = new PersonalMessage(sessionStorage.getItem('nameUser'),document.getElementById("nameUser"));
        	personalMessage.show();
       		playlistList=new playlistList(document.getElementById("playlistList"));
       	   	songsList=new songsList(document.getElementById("songsList"),  document.getElementById("nextButton"), document.getElementById("precButton"), document.getElementById("alertSongsList"));
          	songsList.start();
          	songManager=new songManager(document.getElementById("audioSourceSongInfo"), document.getElementById("audioSongInfo"));
    	};


    	this.refresh=function(){
    		playlistList.show();
    		this.updateAlbumForm();
    	};

    	this.updateAlbumForm=function(){
    		var self = this;
	        makeCall("GET", "GetAlbums", null,
	        function(req) {
	            if (req.readyState == 4) {
		            var message = req.responseText;
		            if (req.status == 200) {
		                var albumsToShow = JSON.parse(req.responseText);
		                if (albumsToShow.length == 0) {
		        	        albumsToShow=null;
		                }
		     			self.buildOptionsAlbum(albumsToShow);
	            	}else{
                		alert(req.status+" "+req.responseText);
                	}
	            } 
	        }
	        );
    	}

    	this.buildOptionsAlbum=function(albums){
    		var optionAlbum=document.getElementById("AlbumTitleSelect");
    		optionAlbum.innerHTML="";
    		var option, text;
    		option= document.createElement("option");
    		text=document.createTextNode("Scegli album");
    		option.appendChild(text);
    		option.selected = "true";
  			option.disable = "true";
  			option.hidden = "true";
  			optionAlbum.appendChild(option);
    		if(albums==null){
    			return;
    		}
    		albums.forEach(function(album){
    			option= document.createElement("option");
    			text=document.createTextNode(album.nameAlbum+"-------"+album.nameSinger);
    			option.appendChild(text);
    			option.value=album.idAlbum;
    			optionAlbum.appendChild(option);
    		})
    	}

    	this.updateSongNotInPlaylist=function(){
    		document.getElementById("songNotInPlaylistAlert").style.display="none";
    		var self = this;
    		if(playlistList.idPlaylistClicked==-1){
    			return;
    		}
	        makeCall("GET", "GetSongsNotIn?idPlaylist="+playlistList.idPlaylistClicked, null,
	        function(req) {
	            if (req.readyState == 4) {
		            var message = req.responseText;
		            if (req.status == 200) {
		                var songsNotIn = JSON.parse(req.responseText);
		                if (songsNotIn.length == 0) {
		        	        document.getElementById("songNotInPlaylistAlert").style.display="block";
		                    return;
		                }
		            	self.buildLiSongsNotIn(songsNotIn);     
	            	}else{
                		alert(req.status+" "+req.responseText);
                	}
	            }
	        }
	        );
    	}

    	this.buildLiSongsNotIn=function(songs){
    		var container=document.getElementById("songNotInPlaylist");
    		container.innerHTML="";
    		var li, detail, summary, form, button, text;
    		songs.forEach(function(song){
    			li= document.createElement("li");
    			detail= document.createElement("details");
    			li.appendChild(detail);
    			summary= document.createElement("summary");
    			text= document.createTextNode(song.title+" - "+song.album.nameAlbum+" - "+song.album.nameSinger);
    			summary.appendChild(text);
    			detail.appendChild(summary);
    			form= document.createElement("form");
    			button= document.createElement("button");
    			text=document.createTextNode("Aggiungi questa canzone alla playlist");
    			button.appendChild(text);
    			button.value=song.idSong;
    			form.appendChild(button);
    			detail.appendChild(form);

    			container.appendChild(li);
    			button.addEventListener("click", (e)=>{
    				
				    makeCall("POST", 'AddSongToPlaylist?idSong='+e.target.getAttribute("value")+"&idPlaylist="+playlistList.idPlaylistClicked, null,
				        function(req) {
				          if (req.readyState == XMLHttpRequest.DONE) {
				            var message = req.responseText;
				            document.getElementById('songNotInPlaylistNotify').style.display='block';
				            switch (req.status) {
				              case 200:
				   				songsList.show(playlistList.idPlaylistClicked);
				   				pageOrchestrator.updateSongNotInPlaylist();

				            	document.getElementById("songNotInPlaylistNotify").textContent = "Song aggiunta!";	//da pulire quando carico la pagina!
				                break;
				              case 400: // bad request
				                document.getElementById("songNotInPlaylistNotify").textContent = message;
				                break;
				              case 401: // unauthorized
				                  document.getElementById("songNotInPlaylistNotify").textContent = message;
				                  break;
				              case 500: // server error
				            	document.getElementById("songNotInPlaylistNotify").textContent = message;
				                break;
				            }
				            setTimeout(function(){document.getElementById('songNotInPlaylistNotify').style.display='none';},3000);
				          }
				        }
				      );

    			});
    		});
    	}
    }


    function playlistList(_listContainer){
    	this.listContainer=_listContainer;
    	this.firstPlaylistId=null;
    	this.idPlaylistClicked=-1;
    	this.playlist=null;

    	this.show = function(){
    		var self = this;
	        makeCall("GET", "GetPlaylists", null,
	        function(req) {
	            if (req.readyState == XMLHttpRequest.DONE) {
		            var message = req.responseText;
		            if (req.status == 200) {
		                var playlistToShow = JSON.parse(req.responseText);
		                if (playlistToShow.length == 0) {
		        	        //self.alert.textContent = "No playlistList yet!";
		        	        self.firstPlaylistId=-1;
		                }else{
		                	self.firstPlaylistId=playlistToShow[0].idPlaylist;
		                }
		                self.playlist=playlistToShow;
		                
		                self.update(playlistToShow); 
		                songsList.show(self.firstPlaylistId);	//autoclick first playlist 
	            	}else{
                		alert(req.status+" "+req.responseText);
                	}
	            }
	        }
	        );
    	};

    	this.colorSelect= function(idPlaylistSelected){
    		var myLi=this.listContainer.querySelectorAll("li");
    		this.idPlaylistClicked=idPlaylistSelected;
    		sessionStorage.setItem('idPlaylist', this.idPlaylistClicked);
    		for(var i=0; i< myLi.length; i++){
    			var anchors= myLi[i].querySelectorAll("a");
    			if(anchors[0].getAttribute("playlistId")==idPlaylistSelected){
    				myLi[i].className="evidenzia";
    			}
    			else{
    				myLi[i].className="";
    			}
    		}
    	}

    	this.update = function(arrayPlaylists){
    		var li, anchor, linkText;
    		this.listContainer.innerHTML = ""; // empty the list body
    		var self=this;

    		arrayPlaylists.forEach(function(playlist){
    			li = document.createElement("li");
    			anchor=document.createElement("a");
    			li.appendChild(anchor);
    			linkText=document.createTextNode(playlist.namePlaylist+" ("+playlist.date+")");
    			anchor.appendChild(linkText);
    			anchor.setAttribute("playlistId", playlist.idPlaylist);
    			anchor.addEventListener("click", (e)=>{
    				songsList.show(e.target.getAttribute("playlistId"));	//mostro le canzoni della playlist selezionata

    			}, false);
    			anchor.href="#"
    			self.listContainer.appendChild(li);
    		})
    	};

    	this.getPlaylistWithId=function(idPlay){
    		for(var i=0; i<this.playlist.length; i++){
    			if(this.playlist[i].idPlaylist== idPlay){
    				return this.playlist[i];
    			}
    		}
    		return null;
    	}

    }


    function songsList( _containerSong, _buttonNext,  _buttonPrec, _alertSongsList){
    	this.idPlaylist=null;
    	this.containerSong= _containerSong;
    	this.buttonPrec=_buttonPrec;
    	this.buttonNext=_buttonNext;
    	this.songs=null;
    	this.firstNextIndex=0;
    	this.alert=_alertSongsList;
    	this.songSelect=null;

    	this.start=function(){
    		this.alert.style.display="none";
    		this.buttonPrec.addEventListener("click", (e)=>{this.precSongs();},false);
    		this.buttonNext.addEventListener("click", (e)=>{this.nextSongs();},false);
    	};

    	this.show=function(idPToShow){
    		if(idPToShow==-1){
    			this.alert.style.display="block";
                document.getElementById("ordersongs").style.visibility="hidden";
                //var songToDisplay=null;
                songManager.show(null);
                this.buttonNext.style.visibility="hidden";
                this.buttonPrec.style.visibility="hidden";
                return;
    		}
    		playlistList.colorSelect(idPToShow);
    		var self = this;
    		makeCall("GET", "GetPlaylistById?idPlaylist=" + idPToShow, null,
            function(req) {
              if (req.readyState == XMLHttpRequest.DONE) {
                var message = req.responseText;
                if (req.status == 200) {
                  	var songs = JSON.parse(req.responseText);
                 	var songToDisplay;
                  	if(songs.length==0){
                  		songToDisplay=null;
                  		self.alert.style.display="block";
                  		document.getElementById("ordersongs").style.visibility="hidden";
                 	}
                 	else{
                 		songToDisplay=songs[0];
                 		self.alert.style.display="none";
                 		document.getElementById("ordersongs").style.visibility="visible";
                 	}
                 	self.update(songs);
                  	self.display();
                  	songManager.show(songToDisplay);
                  
                }else{
                	alert(req.status+" "+req.responseText);
                }
              }
            }
          );	
    		pageOrchestrator.updateSongNotInPlaylist();
    	};

    	this.display=function(){
    		var section, anchor,  image, title3, title2, para1, para2;
    		var title, year, albumName, singer;
    		this.containerSong.innerHTML="";
    		for(var i=this.firstNextIndex; (i<this.firstNextIndex+5 && i<this.songs.length); i++){
    			section = document.createElement("section");
    			anchor=document.createElement("a");
    			section.appendChild(anchor);
    			image=document.createElement("img");
    			anchor.appendChild(image);
    			title3=document.createElement("h3");
    			para1=document.createElement("p");
    			title=document.createTextNode(this.songs[i].title);
    			title3.appendChild(title);
    			year=document.createTextNode(this.songs[i].album.yearPublication);
    			para1.appendChild(year);
    			title3.appendChild(para1);
    			anchor.appendChild(title3);
    			title2=document.createElement("h2");
				para2=document.createElement("p");
				albumName=document.createTextNode(this.songs[i].album.nameAlbum);
				title2.appendChild(albumName);
				singer=document.createTextNode(this.songs[i].album.nameSinger);
				para2.appendChild(singer);
    			title2.appendChild(para2);
    			anchor.appendChild(title2);
    			if(this.songSelect==this.songs[i].idSong){
    				section.setAttribute("id", "highligt");
    			}
    			section.className="song";
    			image.src="/TIW-FinalProject-JavaVersion-Cordioli/GetCover/?img="+this.songs[i].album.imagePath;
    			anchor.setAttribute("songId", this.songs[i].idSong);
    			image.setAttribute("songId", this.songs[i].idSong);
    			title3.setAttribute("songId", this.songs[i].idSong);
    			title2.setAttribute("songId", this.songs[i].idSong);
    			para1.setAttribute("songId", this.songs[i].idSong);
    			para2.setAttribute("songId", this.songs[i].idSong);
    			section.setAttribute("songId", this.songs[i].idSong);
    			anchor.addEventListener("click", (e)=>{
    				var sId=e.target.getAttribute("songId");
    				songManager.show(this.getSongWithId(sId));
    			}, false);
    			anchor.href="#"
    			this.containerSong.appendChild(section);
    		}
    	}

    	this.update=function(songs){
    		this.songs=songs;
    		this.firstNextIndex=0;
    		this.toggleVisibilityButton();
    	};

    	this.nextSongs=function(){
    		this.firstNextIndex=this.firstNextIndex+5;
    		this.toggleVisibilityButton();
    		this.display();
    	};

    	this.precSongs=function(){
    		this.firstNextIndex-=5;
    		this.toggleVisibilityButton();
    		this.display();
    	};

    	this.toggleVisibilityButton=function(){
    		if(this.songs.length>this.firstNextIndex+5){
    			this.buttonNext.style.visibility="visible";
    		}
    		else{
    			this.buttonNext.style.visibility="hidden";
    		}
    		if(0==this.firstNextIndex){
    			this.buttonPrec.style.visibility="hidden";
    		}
    		else{
    			this.buttonPrec.style.visibility="visible";
    		}
    	};

    	this.getSongWithId=function(idSong){
    		for(var i=0; i<this.songs.length; i++){
    			if(this.songs[i].idSong==idSong){
    				return this.songs[i];
    			}
    		}
    		return null;
    	}

    	this.colorSelect=function(idSong){
    		var mySection=this.containerSong.querySelectorAll("section");
    		this.songSelect=idSong;
    		for(var i=0; i< mySection.length; i++){
    			if(mySection[i].getAttribute("songId")==idSong){
    				mySection[i].setAttribute("id", "highligt");
    			}
    			else{
    				mySection[i].setAttribute("id", "");
    			}
    		}
    	}
    }

    function songManager( _audioSourceSong, _audioSong){
    	this.audioSourceSong=_audioSourceSong;
    	this.audioSong=_audioSong;
    	
    	this.show =function(song){
    		if(song==null){
    			this.audioSong.style.visibility="hidden";
    			this.audioSourceSong.src="";
    			return;
    		}
    		this.audioSong.style.visibility="visible";
    		this.display(song);
    	};

    	this.display=function(song){
    		songsList.colorSelect(song.idSong);
    		this.audioSourceSong.src="/TIW-FinalProject-JavaVersion-Cordioli/GetSong/?songPath="+song.songPath;
    		this.audioSong.load();
    		//this.audioSong.play();
    	};
    }

    document.getElementById("addAlbumButton").addEventListener('click', (e) => {
	    var form = document.getElementById("NewAlbumForm");
	    if (form.checkValidity()) {
	      makeCall("POST", 'CreateAlbum', document.getElementById("NewAlbumForm"),
	        function(req) {
	          if (req.readyState == XMLHttpRequest.DONE) {
	            var message = req.responseText;
	            document.getElementById('albumNotify').style.display='block';
	            switch (req.status) {
	              case 200:
	   				pageOrchestrator.updateAlbumForm();
	            	document.getElementById("albumNotify").textContent = "Album aggiunto!";	//da pulire quando carico la pagina!
	                break;
	              case 400: // bad request
	                document.getElementById("albumNotify").textContent = message;
	                break;
	              case 401: // unauthorized
	                  document.getElementById("albumNotify").textContent = message;
	                  break;
	              case 500: // server error
	            	document.getElementById("albumNotify").textContent = message;
	                break;
	            }
	            setTimeout(function(){document.getElementById('albumNotify').style.display='none';},3000);
	          }
	        }
	      );
	    } else {
	    	form.reportValidity();
	    }
  	});


  	document.getElementById("addSongButton").addEventListener('click', (e) => {
	    var form = document.getElementById("NewSongForm");
	    if (form.checkValidity()) {
	      makeCall("POST", 'CreateSong', document.getElementById("NewSongForm"),
	        function(req) {
	          if (req.readyState == XMLHttpRequest.DONE) {
	            var message = req.responseText;
	            document.getElementById('songNotify').style.display='block';
	            switch (req.status) {
	              case 200:
	   				pageOrchestrator.updateSongNotInPlaylist();
	            	document.getElementById("songNotify").textContent = "Canzone aggiunta!";	//da pulire quando carico la pagina!
	                break;
	              case 400: // bad request
	                document.getElementById("songNotify").textContent = message;
	                break;
	              case 401: // unauthorized
	                  document.getElementById("songNotify").textContent = message;
	                  break;
	              case 500: // server error
	            	document.getElementById("songNotify").textContent = message;
	                break;
	            }
	            setTimeout(function(){document.getElementById('songNotify').style.display='none';},3000);
	          }
	        }
	      );
	    } else {
	    	form.reportValidity();
	    }
  	});


  	document.getElementById("ordersongs").addEventListener("click", (e)=>{
  		sessionStorage.setItem('namePlaylist', playlistList.getPlaylistWithId(sessionStorage.getItem("idPlaylist")).namePlaylist);
  		window.location.href = "order.html";
  	});


  	document.getElementById("addPlaylistButton").addEventListener('click', (e) => {
  		var form = document.getElementById("NewPlaylistForm");
	    var valueName=document.getElementById("playlistname").value;
	    if (form.checkValidity()) {
	    	form.reset();
	      makeCall("POST", 'CreatePlaylist?namePlaylist='+valueName, null,
	        function(req) {
	          if (req.readyState == XMLHttpRequest.DONE) {
	            var message = req.responseText;
	            document.getElementById('playlistNotify').style.display='block';
	            switch (req.status) {
	              case 200:
	   				pageOrchestrator.refresh();
	            	document.getElementById("playlistNotify").textContent = "Playlist aggiunta!";	//da pulire quando carico la pagina!
	                break;
	              case 400: // bad request
	                document.getElementById("playlistNotify").textContent = message;
	                break;
	              case 401: // unauthorized
	                  document.getElementById("playlistNotify").textContent = message;
	                  break;
	              case 500: // server error
	            	document.getElementById("playlistNotify").textContent = message;
	                break;
	            }
	            setTimeout(function(){document.getElementById('playlistNotify').style.display='none';},3000);
	          }
	        }
	      );
	    } else {
	    	form.reportValidity();
	    }
  	});


})();
var socket = io("http://localhost:9092");

// room join request 
document.getElementById("join").addEventListener("click", ()=>{
    let msg = {
        name : document.getElementById("name").value,
        msg : document.getElementById("room").value
    };

    sessionStorage.setItem("name",msg.name);
    socket.emit("joinRoom",msg);
});

// room leave request
document.getElementById("leave").addEventListener("click", ()=>{
    let msg = {
        name : sessionStorage.getItem("name"),
        msg : document.getElementById("room").value
    };

    socket.emit("leaveRoom",msg);
});

// message send request
document.getElementById("send").addEventListener("click", ()=>{

    var msg = document.getElementById("msgBox").value

    if(msg != ""){
        msg = {
            name : sessionStorage.getItem("name"),
            msg : msg
        };   
        socket.emit("msg",msg);
    }
});

// message receive
socket.on("message",(msg)=>{
    let d = document.createElement("div");
    d.innerHTML = `<b class="inline-block mt-2">${msg.name}: &nbsp;</b><p class="inline-block">${msg.msg}</p>
            <div class="text-slate-500">${new Date().toLocaleTimeString()}</div>`;
    document.getElementById("screen").append(d);
})

// join message receive
socket.on("joinMsg",(msg)=>{
    document.getElementById("room_name").innerHTML = document.getElementById("room").value;
    let d = document.createElement("div");
    d.innerHTML = `<p style="color:lime;">${msg}</p><div class="text-slate-500">${new Date().toLocaleTimeString()}</div>`;
    document.getElementById("screen").append(d);
});

// leave message receive
socket.on("leftMsg",(msg)=>{
    let d = document.createElement("div");
    d.innerHTML = `<p style="color:coral;">${msg}</p><div class="text-slate-500">${new Date().toLocaleTimeString()}</div>`;
    document.getElementById("screen").append(d);
    console.log(msg);    
});

// hiding login page
document.querySelector("#join").addEventListener("click",()=>{
    let name = document.getElementById("name").value
    let room = document.getElementById("room").value
    
    if(name == "" || room == ""){
        alert("please Enter required parameters");
    }
    else{
        document.getElementById("login_page").classList.add("hidden");
    }
});

// appearing login page
document.querySelector("#leave").addEventListener("click",()=>{
    setTimeout(()=>{
        document.querySelector("#login_page").classList.remove("hidden");
    },1000);
    
});
    var data = [10, 15, 30, 50, 80, 65, 55, 30, 20, 10, 8]; // <- A

    function render(data) { // <- B
        // Enter
        d3.select("body").selectAll("div.h-bar") // <- C
            .data(data) // <- D
            .enter() // <- E
                .append("div") // <- F
                    .attr("class", "h-bar")
                .append("span"); // <- G

        // Update
        d3.select("body").selectAll("div.h-bar")
            .data(data) 
                .style("width", function (d, i) { // <- H
                    console.log("update:", d, i);
                    return (d * 3) + "px";
                })
                .select("span") // <- I
                    .text(function (d) {
                        return d;
                    });
                
        // Exit
        d3.select("body").selectAll("div.h-bar")
            .data(data)
            .exit() // <- J
                .remove();        
    }

    setInterval(function () { // <- K
        data.shift();
        data.push(Math.round(Math.random() * 100));
        render(data);
    }, 1500);

    render(data);

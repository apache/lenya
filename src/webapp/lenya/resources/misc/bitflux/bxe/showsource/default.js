        // Detect and switch the display of CDATA and comments from an inline view
        //  to a block view if the comment or CDATA is multi-line.
        function f(e)
        {
          // if this element is an inline comment, and contains more than a single
          //  line, turn it into a block comment.
          if (e.id == "ci") {
            if (e.childNodes[1].innerHTML.indexOf("\n") > 0)
              fix(e, "cb");
          }
          
          // if this element is an inline cdata, and contains more than a single
          //  line, turn it into a block cdata.
          if (e.id == "di") {
            if (e.childNodes[1].innerHTML.indexOf("\n") > 0)
              fix(e, "db");
          }
          
          // remove the id since we only used it for cleanup
          e.id = "";
        }
        
        // Fix up the element as a "block" display and enable expand/collapse on it
        function fix(e, cl)
        {
          // change the class name and display value
          e.id = cl;
          e.style.display = "block";
          
          // mark the comment or cdata display as a expandable container
          j = e.parentNode.childNodes[1];
          j.id = "c";

          // find the +/- symbol and make it visible - the dummy link enables tabbing
          k = j.childNodes[1];
          k.style.visibility = "visible";
          k.href = "#";
        }

        // Change the +/- symbol and hide the children.  This function works on "element"
        //  displays
        function ch(e)
        {
          // find the +/- symbol
          mark = e.childNodes[0].childNodes[0];
          
          // if it is already collapsed, expand it by showing the children
          if (mark.innerHTML == "+")
          {
            mark.innerHTML = "-";
            //for (var i = 1; i < e.childNodes.length; i++)
            //  e.childNodes[i].style.display = "block";
            e.childNodes[1].style.display = "block";
          }
          
          // if it is expanded, collapse it by hiding the children
          else if (mark.innerHTML == "-")
          {
            mark.innerHTML = "+";
            e.childNodes[1].style.display="none";
            //for (var i = 1; i < e.childNodes[3].childNodes.length; i++) {
            //  e.childNodes[1].childNodes[i].style.display="none";
            //}
          }
        }
        
        // Change the +/- symbol and hide the children.  This function work on "comment"
        //  and "cdata" displays
        function ch2(e)
        {
          // find the +/- symbol, and the "PRE" element that contains the content
          mark = e.childNodes[1].childNodes[1];
          contents = e.childNodes[1];
          
          // if it is already collapsed, expand it by showing the children
          if (mark.innerHTML == "+")
          {
            alert("Van + naar - binnen ch2()");
            mark.innerHTML = "-";
            // restore the correct "block"/"inline" display type to the PRE
            if (contents.id == "db" || contents.id == "cb")
              contents.style.display = "block";
            else contents.style.display = "inline";
          }
          
          // if it is expanded, collapse it by hiding the children
          else if (mark.innerHTML == "-")
          {
            alert("Van - naar + binnen ch2()");
            mark.innerHTML = "+";
            contents.style.display = "none";
          }
        }
        
        // Handle a mouse click
        function cl(evt)
        {
          
          e = evt.target;
          while (e.tagName != "div") {e = e.parentNode}
          
          // make sure we are handling clicks upon expandable container elements
          if (e.id != "c")
          {
            e = e.parentNode;
            if (e.id != "c")
            {
              return;
            }
          }
          e = e.parentNode;
          
          // call the correct funtion to change the collapse/expand state and display
          if (e.id == "e")
            ch(e);
          if (e.id == "k")
            ch2(e);
        }
        
        // Erase bogus link info from the status window
        function h()
        {
          window.status=" ";
        }

        // Set the onclick handler
        document.addEventListener('click', cl, true);
        

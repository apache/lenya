/**************************
* Undo Stuff              *
***************************/

function BX_undo_save()
{
    if (BX_undo_counter > 0)
    {
        BX_doc_changed = true;
    }

    BX_undo_counter++;
    BX_undo_max = BX_undo_counter;
    BX_undo_buffer[BX_undo_counter] = BX_transformLocation.cloneNode(true);
    //	window.defaultStatus = "undo save: " + BX_undo_counter;
    if (BX_undo_buffer.length > 10)
    {
        BX_undo_buffer[BX_undo_counter - 10] = null;
    }

    //    window.defaultStatus = "undo saved: " + BX_undo_counter;
    BX_undo_updateButtons();
}

function BX_undo_undo()
{
    if (BX_undo_buffer[BX_undo_counter - 1])
    {

        BX_undo_counter--;

        //        window.defaultStatus = "undo undo: " + BX_undo_counter;
        var newNode = BX_undo_buffer[BX_undo_counter].cloneNode(true);
        BX_transformLocation.parentNode.replaceChild(newNode,BX_transformLocation);
        BX_transformLocation =  document.getElementById("transformlocation");
        BX_addEvents();
        BX_cursor = document.getElementById("bx_cursor");
        if (BX_cursor)
        {
            BX_range.selectNode(BX_cursor);
            BX_range.collapse(true);
        }
    }

    BX_undo_updateButtons();
}

function BX_undo_redo()
{

    if (BX_undo_buffer[BX_undo_counter + 1] && (BX_undo_counter + 1) <= BX_undo_max )
    {


        BX_undo_counter++;
        //        window.defaultStatus = "undo undo: " + BX_undo_counter;
        var newNode = BX_undo_buffer[BX_undo_counter].cloneNode(true);
        BX_transformLocation.parentNode.replaceChild(newNode,BX_transformLocation);

        BX_transformLocation =  document.getElementById("transformlocation");

        BX_addEvents();
        BX_cursor = document.getElementById("bx_cursor");
        if (BX_cursor)
        {
            BX_range.selectNode(BX_cursor);
            BX_range.collapse(true);
        }

    }
    BX_undo_updateButtons();
}

function BX_undo_updateButtons()
{
    if (BX_undo_counter == BX_undo_max)
    {
        document.getElementById("but_redo").src="./"+BX_root_dir+"/img/wt_redo_p.gif";
    }
    else
    {
        document.getElementById("but_redo").src="./"+BX_root_dir+"/img/wt_redo_n.gif";
    }
    if (! BX_undo_buffer[BX_undo_counter - 1])
    {
        document.getElementById("but_undo").src="./"+BX_root_dir+"/img/wt_undo_p.gif";
    }
    else
    {
        document.getElementById("but_undo").src="./"+BX_root_dir+"/img/wt_undo_n.gif";
    }
}

// for whatever reason, jsdoc needs this line

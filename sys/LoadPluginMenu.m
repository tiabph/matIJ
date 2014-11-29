function handles = LoadPluginMenu(hmenu, filterList, handles)
    userdata = cell(size(filterList,1),3);
    userdata(:,1:2) = filterList;
    for m=1:size(filterList,1)
        temphandle = uimenu(hmenu,'Label',userdata{m,1}, ...
            'Tag',userdata{m,1},'Callback',@PluginMenu_Callback); 
        userdata{m,3} = temphandle;
    end
    handles.PluginInfo = userdata;
end


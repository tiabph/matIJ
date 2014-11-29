function PluginMenu_Callback(hObject, eventdata, handles)
    if(nargin<3)
        handles = guidata(hObject);
    end
    menuidx = -1;
    for m=1:size(handles.PluginInfo, 1)
        if(handles.PluginInfo{m,3} == hObject)
            menuidx = m;
            break;
        end
    end

    if(menuidx>0)
        mname = handles.PluginInfo{menuidx,1};
        mfunc = handles.PluginInfo{menuidx,2};
        disp(['menu ' mname ' clicked']);
    else
        return;
    end
    
    %process image
    timg = MIJ.getCurrentImage();
    imgtype = class(timg);
    timg = im2double(timg);
    resultimg = mfunc(timg);
    if(strcmp(imgtype,'int16'))
        im2uint8(resultimg)
    end
    if(strcmp(imgtype,'int32'))
        im2uint16(resultimg)
    end
    MIJ.createImage(resultimg);
end
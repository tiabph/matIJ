function result = loadParameters(configFile, result)
    if(nargin<2)
        result = [];
    end
    
    fp = fopen(configFile,'r');
    while(1)
        tstr=fgetl(fp);
        if(isnumeric(tstr))
            break;
        else
            tstr = strtrim(tstr);
            if(tstr(1)~='#' && ~isempty(tstr))
                dpos = find(tstr==':',1);
                filedName = strtrim(tstr(1 : dpos-1));
                value = strtrim(tstr(dpos+1 : end));
                result = setfield(result, filedName, value);
            end
        end
    end
    fclose(fp);
end

function writeParameters(configFile, param)
%TODO: keep origin informations
    fp = fopen(configFile,'w');
    fieldList = fieldnames(param);
    for m = 1:length(fieldList)
        fprintf(fp, '%s : %s\r\n', fieldList{m}, getfield(param, fieldList{m}));
    end
    fclose(fp);
end
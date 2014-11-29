function filterList = LoadFilters(searchPath)
    fileList = dir(fullfile(searchPath, '*.m'));
    filterList = cell(length(fileList),2);
    for m=1:length(fileList)
        tfile = fileList(m);
        [~,name]=fileparts(tfile.name);
        filterList{m,1} = name;
    end
    filterList(:,1) = sortrows(filterList(:,1));
    for m=1:length(fileList)
        filterList{m,2} = str2func(filterList{m,1});
    end
end
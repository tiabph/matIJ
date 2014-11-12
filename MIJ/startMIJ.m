function startMIJ(ijPath)
    %check parameters
    if(nargin<1)
        ijPath = [];
    end
    
    %check class path
    classList = javaclasspath('-all');
    mijLost = ~any(~cellfun(@isempty,strfind(classList, '\mij.jar')));
    ijLost = ~any(~cellfun(@isempty,strfind(classList, '\ij.jar')));
    
    %add class path
    mfilepath = mfilename('fullpath');
    [pathstr] = fileparts(mfilepath);
    javaPath = [pathstr '\..\java\'];
    if(mijLost)
        javaaddpath([javaPath 'mij.jar']);
    end
    if(ijLost)
        javaaddpath([javaPath 'ij.jar']);
    end
    
    %start ImageJ
    if(isempty(ijPath))
        MIJ.start
    else
        MIJ.start(ijPath)
    end
end
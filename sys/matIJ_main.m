function varargout = matIJ_main(varargin)
% MATIJ_MAIN M-file for matIJ_main.fig
%      MATIJ_MAIN, by itself, creates a new MATIJ_MAIN or raises the existing
%      singleton*.
%
%      H = MATIJ_MAIN returns the handle to a new MATIJ_MAIN or the handle to
%      the existing singleton*.
%
%      MATIJ_MAIN('CALLBACK',hObject,eventData,handles,...) calls the local
%      function named CALLBACK in MATIJ_MAIN.M with the given input arguments.
%
%      MATIJ_MAIN('Property','Value',...) creates a new MATIJ_MAIN or raises the
%      existing singleton*.  Starting from the left, property value pairs are
%      applied to the GUI before matIJ_main_OpeningFcn gets called.  An
%      unrecognized property name or invalid value makes property application
%      stop.  All inputs are passed to matIJ_main_OpeningFcn via varargin.
%
%      *See GUI Options on GUIDE's Tools menu.  Choose "GUI allows only one
%      instance to run (singleton)".
%
% See also: GUIDE, GUIDATA, GUIHANDLES

% Edit the above text to modify the response to help matIJ_main

% Last Modified by GUIDE v2.5 12-Nov-2014 13:25:32

% Begin initialization code - DO NOT EDIT
gui_Singleton = 1;
gui_State = struct('gui_Name',       mfilename, ...
                   'gui_Singleton',  gui_Singleton, ...
                   'gui_OpeningFcn', @matIJ_main_OpeningFcn, ...
                   'gui_OutputFcn',  @matIJ_main_OutputFcn, ...
                   'gui_LayoutFcn',  [] , ...
                   'gui_Callback',   []);
if nargin && ischar(varargin{1})
    gui_State.gui_Callback = str2func(varargin{1});
end

if nargout
    [varargout{1:nargout}] = gui_mainfcn(gui_State, varargin{:});
else
    gui_mainfcn(gui_State, varargin{:});
end
% End initialization code - DO NOT EDIT


% --- Executes just before matIJ_main is made visible.
function matIJ_main_OpeningFcn(hObject, eventdata, handles, varargin)
% This function has no output args, see OutputFcn.
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)
% varargin   command line arguments to matIJ_main (see VARARGIN)

% Choose default command line output for matIJ_main
handles.output = hObject;

[pathstr] = fileparts(mfilename('fullpath'));
rootPath = [pathstr '\..\'];
parameters = loadParameters([rootPath 'sys\config.txt']);
set(hObject,'Name',['matIJ - ' parameters.version]);
startMIJ(parameters.ImageJPath);
handles.parameters = parameters;
handles.rootPath = rootPath;

% Update handles structure
guidata(hObject, handles);

% UIWAIT makes matIJ_main wait for user response (see UIRESUME)
% uiwait(handles.matIJ);


% --- Outputs from this function are returned to the command line.
function varargout = matIJ_main_OutputFcn(hObject, eventdata, handles) 
% varargout  cell array for returning output args (see VARARGOUT);
% hObject    handle to figure
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

% Get default command line output from handles structure
varargout{1} = handles.output;


% --- Executes during object creation, after setting all properties.
function matIJ_CreateFcn(hObject, eventdata, handles)
% hObject    handle to matIJ (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    empty - handles not created until after all CreateFcns called


% --- Executes when user attempts to close matIJ.
function matIJ_CloseRequestFcn(hObject, eventdata, handles)
% hObject    handle to matIJ (see GCBO)
% eventdata  reserved - to be defined in a future version of MATLAB
% handles    structure with handles and user data (see GUIDATA)

stopMIJ();
% Hint: delete(hObject) closes the figure
delete(hObject);

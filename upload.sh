#!/bin/bash
T=`date '+%Y-%m-%d_%H-%M-%S'`
java -cp soonr.jar jtcom.lib.srv.ConnectionHandler up ./log TrackMan/${T}

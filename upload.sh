#!/bin/bash
T=`date '+%Y-%m-%d_%H-%M-%S'`
java -cp soonr.jar jtcom.lib.srv.ConnectionHandler up ./log TrackMan/${T}
echo "view details https://us.workplace.datto.com/filelink/6813-7a27b0db-919db10462-2"
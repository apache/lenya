date=`date +%Y%m%d%H%M`; sed -e "s/var BXE_BUILD = .*/var BXE_BUILD = \"$date\"/" bxeLoader.js > bxeLoader.js.new ; mv bxeLoader.js.new bxeLoader.js

#  Licensed to the Apache Software Foundation (ASF) under one or more
#  contributor license agreements.  See the NOTICE file distributed with
#  this work for additional information regarding copyright ownership.
#  The ASF licenses this file to You under the Apache License, Version 2.0
#  (the "License"); you may not use this file except in compliance with
#  the License.  You may obtain a copy of the License at
#  
#  http://www.apache.org/licenses/LICENSE-2.0
#  
#  Unless required by applicable law or agreed to in writing, software
#  distributed under the License is distributed on an "AS IS" BASIS,
#  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
#  See the License for the specific language governing permissions and
#  limitations under the License.

#
# The script computes the maximum for each minute interval and outputs only a single
# log entry for each minute. This way the performance of the SVG diagram generation
# can be improved.
#
# Usage: ruby compress_log.rb sessions.log > sessions-compressed.log
#

def compress_log(logfile_name)
  File.open(logfile_name, "r") do |logfile|
    max = 0
    prev_minute = 0
    prev_hour = ""
    logfile.each_line { |line|
      re = Regexp.new(/([0-9]{4}-[0-9]{2}-[0-9]{2}) ([0-9]{2}):([0-9]{2}):([0-9]{2}),[0-9]{3} Sessions: ([0-9]{1,10})/)
      data = re.match(line)
      if data
        if prev_hour == ""
          prev_hour = data[2]
        end
        hour = data[2]
        minute = data[3]
        if hour != prev_hour || minute != prev_minute
          puts data[1] + " " + prev_hour + ":" + prev_minute.to_s + ":00,000" + " Sessions: " + max.to_s
          prev_minute = minute
          prev_hour = hour
          max = 0
        end
        sessions = data[5].to_i
        if sessions > max
          max = sessions
        end
      end
    }
  end
end

compress_log(ARGV[0])

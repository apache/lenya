def compress_log(logfile_name)
  puts "Hello"
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

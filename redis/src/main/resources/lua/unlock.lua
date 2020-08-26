if (redis.call('hexists', KEYS[1], ARGV[2]) == 0)
then 
	return nil;
end;
local counter = redis.call('hincrby', KEYS[1], ARGV[2], -1);
if (counter > 0)
then
	redis.call('pexpire', KEYS[1], ARGV[1]);
	return 0;
else
	redis.call('del', KEYS[1]);
	return 1;
end;
return nil;
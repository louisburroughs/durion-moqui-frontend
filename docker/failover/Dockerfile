FROM alpine:3.18

# Install required packages
RUN apk add --no-cache curl jq bash redis

# Copy failover monitoring script
COPY docker/scripts/failover-monitor.sh /failover-monitor.sh
RUN chmod +x /failover-monitor.sh

# Set working directory
WORKDIR /opt/failover

# Run failover monitor
CMD ["/failover-monitor.sh"]

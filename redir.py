TARGET_SCHEME = "http"
TARGET_PORT = 8888
TARGET_HOST = "localhost"
ORIG_DOMAIN = "nianticlabs.com"

def request(context, flow):
    if flow.request.pretty_host.endswith(ORIG_DOMAIN):
        flow.request.scheme = TARGET_SCHEME
        flow.request.port = TARGET_PORT
        flow.request.host = TARGET_HOST

    # stop data mining me pls
    if flow.request.pretty_host.endswith("crittercism.com"):
        flow.request.host = "localhost"
    #if flow.request.pretty_host.endswith("unity3d.com"):
    #    flow.request.host = "localhost"
    #if flow.request.pretty_host.endswith("upsight-api.com"):
    #    flow.request.host = "localhost"


def response(context, flow):
    flow.response.scheme = "https"

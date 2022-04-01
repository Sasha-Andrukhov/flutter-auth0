part of auth0_auth;

class Auth0Exeption implements Exception {
  final String? name;
  final dynamic? description;
  final dynamic? stackTrace;
  Auth0Exeption(
      {this.name = 'a0.response.invalid', this.description = 'unknown error', this.stackTrace = ""});

  String toString() {
    return '$name: $description: $stackTrace';
  }
}

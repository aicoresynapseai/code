import sys
import re
import json
from collections import defaultdict
from bs4 import BeautifulSoup # Required for parsing XML for latest versions (mock)

def parse_maven_dependency_tree(tree_output):
    """
    Parses the output of `mvn dependency:tree` to extract GAV coordinates.
    Example line: `[INFO] +- org.apache.commons:commons-collections4:jar:4.0:compile`
    """
    dependencies = []
    # Regex to capture GAV for direct dependencies and common transitive formats
    # It's simplified for typical `dependency:tree` output
    gav_pattern = re.compile(r"^\s*[\\|+-]+\s*([a-zA-Z0-9._-]+):([a-zA-Z0-9._-]+):jar:([0-9.]+)(?::([a-zA-Z]+))?$")

    for line in tree_output.splitlines():
        match = gav_pattern.match(line)
        if match:
            group_id = match.group(1)
            artifact_id = match.group(2)
            version = match.group(3)
            # Avoid duplicates if a dependency appears multiple times (e.g., different scopes or transitive)
            if {"group_id": group_id, "artifact_id": artifact_id, "version": version} not in dependencies:
                dependencies.append({
                    "group_id": group_id,
                    "artifact_id": artifact_id,
                    "version": version
                })
    return dependencies

def load_vulnerability_db(db_path="security/vulnerability_db.json"):
    """Loads the mock vulnerability database."""
    try:
        with open(db_path, 'r') as f:
            return json.load(f)
    except FileNotFoundError:
        print(f"Error: Vulnerability database not found at {db_path}", file=sys.stderr)
        return []

def get_latest_version_mock(group_id, artifact_id):
    """
    Mocks getting the latest version from Maven Central.
    In a real scenario, this would query Maven Central's API or a similar repository.
    For this demo, we'll hardcode some "latest" versions.
    """
    mock_latest_versions = {
        "org.apache.commons:commons-collections4": "4.4", # Already defined in vulnerability DB, but good for consistency
        "com.google.guava:guava": "32.1.3-jre",
        "org.slf4j:slf4j-api": "2.0.7", # Assume this is current
        "org.junit.jupiter:junit-jupiter-api": "5.10.0", # A newer version than 5.8.1
        "org.junit.jupiter:junit-jupiter-engine": "5.10.0"
    }
    return mock_latest_versions.get(f"{group_id}:{artifact_id}")

def analyze_dependencies(dependencies, vulnerability_db):
    """
    Analyzes dependencies against the vulnerability DB and suggests upgrades.
    This simulates the "AI" part, by applying rules from the mock DB and "knowledge"
    of latest versions.
    """
    recommendations = []
    processed_gavs = set() # To avoid duplicate recommendations for the same GAV

    for dep in dependencies:
        gav = f"{dep['group_id']}:{dep['artifact_id']}:{dep['version']}"
        if gav in processed_gavs:
            continue # Already processed this specific GAV
        processed_gavs.add(gav)

        status = "ok"
        recommended_action = "none"
        recommended_version = dep['version']
        vulnerability_details = ""
        details = ""

        # Check against vulnerability database
        for vuln_entry in vulnerability_db:
            if dep['group_id'] == vuln_entry['group_id'] and \
               dep['artifact_id'] == vuln_entry['artifact_id']:
                if dep['version'] in vuln_entry.get('vulnerable_versions', []):
                    status = "vulnerable"
                    recommended_action = "upgrade"
                    recommended_version = vuln_entry['fixed_version']
                    vulnerability_details = f"{vuln_entry['vulnerability_id']}: {vuln_entry['description']}"
                    details = "Identified vulnerability. Critical upgrade recommended."
                    break # Found a vulnerability, prioritize this recommendation

        # If not vulnerable, check if outdated
        if status == "ok":
            latest_version = get_latest_version_mock(dep['group_id'], dep['artifact_id'])
            if latest_version and latest_version != dep['version']:
                # Simple version comparison for demo. Real scenarios need proper version parsing (e.g., semver)
                if tuple(map(int, dep['version'].split('.'))) < tuple(map(int, latest_version.split('.'))):
                    status = "outdated"
                    recommended_action = "upgrade"
                    recommended_version = latest_version
                    details = "Newer stable version available. Consider upgrading for features/performance."

        recommendations.append({
            "group_id": dep['group_id'],
            "artifact_id": dep['artifact_id'],
            "current_version": dep['version'],
            "status": status,
            "recommended_action": recommended_action,
            "recommended_version": recommended_version,
            "vulnerability_details": vulnerability_details,
            "details": details
        })
    return recommendations

if __name__ == "__main__":
    # Read Maven dependency tree output from stdin
    maven_tree_output = sys.stdin.read()

    # Parse dependencies
    dependencies = parse_maven_dependency_tree(maven_tree_output)

    # Load vulnerability database
    vulnerability_db = load_vulnerability_db()

    # Analyze and get recommendations
    recommendations = analyze_dependencies(dependencies, vulnerability_db)

    # Output recommendations as JSON
    print(json.dumps(recommendations, indent=2))
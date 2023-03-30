import { Select } from "../Select/Select";

const SUPPORTED_FRAMEWORKS = ["React", "Vue", "Svelte"];

export default function TopPanel({
  framework = "React",
  onFrameworkSelected = (framework) => {},
}: {
  framework: string;
  onFrameworkSelected: (framework: string) => void;
}) {
  return (
    <div className="flex flex-row items-center h-12 px-4 bg-gray-700 text-white gap-4">
      <div>Choose the framework</div>
      <Select
        options={SUPPORTED_FRAMEWORKS}
        onSelected={onFrameworkSelected}
        selected={framework}
      />
    </div>
  );
}
